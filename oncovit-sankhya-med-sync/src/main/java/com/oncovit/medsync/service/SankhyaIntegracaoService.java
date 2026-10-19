package com.oncovit.medsync.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oncovit.medsync.domain.enums.TipoMovimentacao;
import com.oncovit.medsync.domain.model.Lote;
import com.oncovit.medsync.domain.model.Movimentacao;
import com.oncovit.medsync.dto.BaixaEstoqueRequest;
import com.oncovit.medsync.dto.BaixaEstoqueResponse;
import com.oncovit.medsync.exception.*;
import com.oncovit.medsync.repository.LoteRepository;
import com.oncovit.medsync.repository.MovimentacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class SankhyaIntegracaoService {

    private static final Logger log = LoggerFactory.getLogger(SankhyaIntegracaoService.class);

    @Value("${sankhya.api.app-key:oncovit-medsync-api-key-2026}")
    private String expectedAppKey;

    @Value("${sankhya.api.token:bearer sankhya-session-jwt-token-example}")
    private String expectedToken;

    private final LoteRepository loteRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final SankhyaAuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    public SankhyaIntegracaoService(LoteRepository loteRepository,
                                   MovimentacaoRepository movimentacaoRepository,
                                   SankhyaAuditLogService auditLogService,
                                   ObjectMapper objectMapper) {
        this.loteRepository = loteRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.auditLogService = auditLogService;
        this.objectMapper = objectMapper;
    }

    /**
     * Valida os cabeçalhos de autenticação da integração Sankhya.
     * Conforme especificação: X-Sankhya-AppKey deve corresponder à chave cadastrada e X-Sankhya-Token
     * deve ser uma string não-vazia válida.
     */
    public void validarCredenciaisSankhya(String appKey, String token) {
        boolean appKeyValida = appKey != null && appKey.equals(expectedAppKey);
        boolean tokenValido = token != null && !token.isBlank();

        if (!appKeyValida || !tokenValido) {
            auditLogService.registrarLog(null, "DESCONHECIDO", 0, "UNAUTHORIZED", 401,
                    "Autenticação recusada: Credenciais Sankhya inválidas ou ausentes.", null, null);
            throw new SankhyaAuthException("Acesso não autorizado: Cabeçalhos X-Sankhya-AppKey ou X-Sankhya-Token inválidos.");
        }
    }

    /**
     * Ponto de entrada chamado pelo Controller validando autenticação e executando baixa.
     */
    @Transactional
    public BaixaEstoqueResponse processarBaixa(String appKey, String token, BaixaEstoqueRequest request) {
        validarCredenciaisSankhya(appKey, token);
        return processarBaixaEstoque(request, LocalDate.now());
    }

    /**
     * Executa a baixa de estoque simulada do Sankhya ERP.
     */
    @Transactional
    public BaixaEstoqueResponse processarBaixaEstoque(BaixaEstoqueRequest request, LocalDate dataReferencia) {
        String payloadRequisicaoJson = serializarJson(request);

        // Validação de entrada
        if (request == null || request.numeroLote() == null || request.numeroLote().isBlank()) {
            auditLogService.registrarLog(null, "DESCONHECIDO", 0, "INVALID_PAYLOAD", 400,
                    "Requisição inválida: Número do lote é obrigatório.", payloadRequisicaoJson, null);
            throw new IllegalArgumentException("Número do lote é obrigatório.");
        }
        if (request.quantidade() == null || request.quantidade() <= 0) {
            Long nuNota = request.numeroNota();
            auditLogService.registrarLog(nuNota, request.numeroLote(), 0, "INVALID_PAYLOAD", 400,
                    "Requisição inválida: Quantidade deve ser maior que zero.", payloadRequisicaoJson, null);
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }

        // Busca do lote
        Lote lote = loteRepository.findByNumeroLoteComMedicamento(request.numeroLote())
                .orElse(null);

        if (lote == null) {
            String msg = "O lote informado (" + request.numeroLote() + ") não foi localizado no catálogo da Oncovit.";
            auditLogService.registrarLog(request.numeroNota(), request.numeroLote(), request.quantidade(),
                    "LOT_NOT_FOUND", 404, msg, payloadRequisicaoJson, null);
            throw new ResourceNotFoundException(msg, "LOT_NOT_FOUND", 404);
        }

        int saldoAnterior = lote.getQuantidadeAtual() != null ? lote.getQuantidadeAtual() : 0;

        try {
            // Executa baixa através do método de negócio da entidade (com barreira sanitária)
            lote.debitarEstoque(request.quantidade(), dataReferencia);
            loteRepository.saveAndFlush(lote);

            // Registra histórico de movimentação
            String docRef = request.numeroNota() != null ? "NF-" + request.numeroNota() : "AVULSO";
            String usuario = request.usuario() != null ? request.usuario() : "sankhya.operador";
            String motivo = request.motivo() != null ? request.motivo() : "Atendimento pedido hospitalar Oncovit";

            Movimentacao mov = new Movimentacao(
                    lote,
                    TipoMovimentacao.BAIXA_SANKHYA_VENDA,
                    request.quantidade(),
                    docRef,
                    motivo,
                    usuario
            );
            movimentacaoRepository.save(mov);

            // Prepara resposta de sucesso
            String protocolo = "SNK-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
                               "-" + (request.numeroNota() != null ? request.numeroNota() : UUID.randomUUID().toString().substring(0, 8));
            String msgSucesso = "Baixa de estoque confirmada com sucesso no Oncovit Med Sync.";

            BaixaEstoqueResponse response = new BaixaEstoqueResponse(
                    "SUCCESS",
                    protocolo,
                    msgSucesso,
                    request.quantidade(),
                    saldoAnterior,
                    lote.getQuantidadeAtual(),
                    LocalDateTime.now()
            );

            String payloadRespostaJson = serializarJson(response);
            auditLogService.registrarLog(request.numeroNota(), lote.getNumeroLote(), request.quantidade(),
                    "SUCCESS", 200, msgSucesso, payloadRequisicaoJson, payloadRespostaJson);

            return response;

        } catch (InsufficientStockException ex) {
            auditLogService.registrarLog(request.numeroNota(), lote.getNumeroLote(), request.quantidade(),
                    "OUT_OF_STOCK", 409, ex.getMessage(), payloadRequisicaoJson, null);
            throw ex;
        } catch (ExpiredLotException ex) {
            auditLogService.registrarLog(request.numeroNota(), lote.getNumeroLote(), request.quantidade(),
                    "EXPIRED_LOT_REJECTED", 422, ex.getMessage(), payloadRequisicaoJson, null);
            throw ex;
        } catch (InvalidLotStatusException ex) {
            auditLogService.registrarLog(request.numeroNota(), lote.getNumeroLote(), request.quantidade(),
                    "LOT_NOT_RELEASED", 422, ex.getMessage(), payloadRequisicaoJson, null);
            throw ex;
        } catch (ConcurrencyFailureException ex) {
            auditLogService.registrarLog(request.numeroNota(), lote.getNumeroLote(), request.quantidade(),
                    "CONCURRENCY_CONFLICT", 409, "Conflito de concorrência detectado no lote " + lote.getNumeroLote(), payloadRequisicaoJson, null);
            throw ex;
        }
    }

    /**
     * Delega a gravação de logs de auditoria ao SankhyaAuditLogService.
     */
    public void gravarLogAuditoria(Long nuNota, String numeroLote, int quantidade,
                                  String statusTransacao, int statusHttp,
                                  String mensagem, String reqPayload, String respPayload) {
        auditLogService.registrarLog(nuNota, numeroLote, quantidade, statusTransacao, statusHttp, mensagem, reqPayload, respPayload);
    }

    private String serializarJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{\"raw\":\"" + String.valueOf(obj) + "\"}";
        }
    }
}
