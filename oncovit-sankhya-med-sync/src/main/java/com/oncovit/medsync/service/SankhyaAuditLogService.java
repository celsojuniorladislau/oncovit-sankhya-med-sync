package com.oncovit.medsync.service;

import com.oncovit.medsync.domain.model.SankhyaSyncLog;
import com.oncovit.medsync.repository.SankhyaSyncLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço autônomo responsável pela persistência do log de auditoria da integração Sankhya ERP.
 * Executa em transação física independente (REQUIRES_NEW) para assegurar que tentativas de acesso
 * não autorizado, violações de barreira sanitária e rejeições por falta de estoque sejam
 * perpetuadas na tabela sankhya_sync_log mesmo quando a transação de baixa de estoque for revertida (rollback).
 */
@Service
public class SankhyaAuditLogService {

    private static final Logger log = LoggerFactory.getLogger(SankhyaAuditLogService.class);
    private static final String ENDPOINT_BAIXA = "/api/integracao/sankhya/baixa-estoque";

    private final SankhyaSyncLogRepository syncLogRepository;

    public SankhyaAuditLogService(SankhyaSyncLogRepository syncLogRepository) {
        this.syncLogRepository = syncLogRepository;
    }

    /**
     * Registra evento de auditoria em transação física independente.
     *
     * @param nuNota          Número único da nota fiscal no Sankhya ERP (pode ser nulo em falhas pré-processamento)
     * @param numeroLote      Número do lote farmacêutico (ou 'DESCONHECIDO')
     * @param quantidade      Quantidade envolvida na operação
     * @param statusTransacao Código mnemônico da operação (ex: SUCCESS, UNAUTHORIZED, LOT_NOT_FOUND, OUT_OF_STOCK, EXPIRED_LOT_REJECTED)
     * @param statusHttp      Código de status HTTP correspondente (ex: 200, 401, 404, 409, 422)
     * @param mensagem        Mensagem descritiva do resultado ou erro
     * @param reqPayload      JSON do payload recebido
     * @param respPayload     JSON do payload retornado (ou nulo em caso de erro)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarLog(Long nuNota, String numeroLote, int quantidade,
                             String statusTransacao, int statusHttp,
                             String mensagem, String reqPayload, String respPayload) {
        try {
            String loteTratado = (numeroLote != null && !numeroLote.isBlank()) ? numeroLote : "DESCONHECIDO";
            SankhyaSyncLog logEntry = new SankhyaSyncLog(
                    ENDPOINT_BAIXA,
                    nuNota,
                    loteTratado,
                    quantidade,
                    statusTransacao,
                    statusHttp,
                    mensagem,
                    reqPayload,
                    respPayload
            );
            syncLogRepository.save(logEntry);
            log.info("Auditoria Sankhya persistida com sucesso [HTTP {} | {} | Lote: {} | NF: {}]",
                    statusHttp, statusTransacao, loteTratado, nuNota);
        } catch (Exception e) {
            log.error("Falha crítica ao persistir log de auditoria Sankhya: {}", e.getMessage(), e);
        }
    }

    /**
     * Alias para compatibilidade com chamadas de gravarLogAuditoria.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void gravarLogAuditoria(Long nuNota, String numeroLote, int quantidade,
                                   String statusTransacao, int statusHttp,
                                   String mensagem, String reqPayload, String respPayload) {
        registrarLog(nuNota, numeroLote, quantidade, statusTransacao, statusHttp, mensagem, reqPayload, respPayload);
    }
}
