package com.oncovit.medsync.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oncovit.medsync.domain.enums.CategoriaMedicamento;
import com.oncovit.medsync.domain.enums.StatusLote;
import com.oncovit.medsync.domain.enums.TipoArmazenamento;
import com.oncovit.medsync.domain.enums.TipoMovimentacao;
import com.oncovit.medsync.domain.model.Lote;
import com.oncovit.medsync.domain.model.Medicamento;
import com.oncovit.medsync.domain.model.Movimentacao;
import com.oncovit.medsync.dto.BaixaEstoqueRequest;
import com.oncovit.medsync.dto.BaixaEstoqueResponse;
import com.oncovit.medsync.exception.*;
import com.oncovit.medsync.repository.LoteRepository;
import com.oncovit.medsync.repository.MovimentacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Serviço: SankhyaIntegracaoService (Webhook ERP, Transações e Logs)")
class SankhyaIntegracaoServiceTest {

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private MovimentacaoRepository movimentacaoRepository;

    @Mock
    private SankhyaAuditLogService auditLogService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private SankhyaIntegracaoService sankhyaService;

    private final LocalDate hoje = LocalDate.of(2026, 10, 19);
    private Medicamento medicamento;
    private Lote loteLiberado;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sankhyaService, "expectedAppKey", "test-app-key");
        ReflectionTestUtils.setField(sankhyaService, "expectedToken", "test-token");

        medicamento = new Medicamento("MED-ONC-001", "Herceptin 440mg", "Trastuzumabe",
                CategoriaMedicamento.ANTICORPO_MONOCLONAL, TipoArmazenamento.CADEIA_DE_FRIO,
                new BigDecimal("2.0"), new BigDecimal("8.0"), "FRASCO_AMPOLA");
        medicamento.setId(1L);

        loteLiberado = new Lote("LOT-HER-2026", medicamento, hoje.minusMonths(2), hoje.plusMonths(8),
                StatusLote.LIBERADO, 100, new BigDecimal("7850.00"), "Roche Farma");
        loteLiberado.setQuantidadeAtual(45);
        loteLiberado.setId(10L);
    }

    @Test
    @DisplayName("Cenário Sucesso: Deve processar baixa de estoque, abater saldo, criar movimentação e logar auditoria")
    void deveProcessarBaixaComSucesso() {
        BaixaEstoqueRequest request = new BaixaEstoqueRequest(
                "MED-ONC-001", "LOT-HER-2026", 10, 105420L,
                "SAIDA_VENDA_HOSPITALAR", "01.234.567/0001-89",
                "Atendimento Pedido Hospital Santa Rita", "sankhya.operador"
        );

        when(loteRepository.findByNumeroLoteComMedicamento("LOT-HER-2026"))
                .thenReturn(Optional.of(loteLiberado));

        BaixaEstoqueResponse response = sankhyaService.processarBaixaEstoque(request, hoje);

        assertNotNull(response);
        assertEquals("SUCCESS", response.status());
        assertEquals(10, response.quantidadeBaixada());
        assertEquals(45, response.saldoAnterior());
        assertEquals(35, response.saldoRestanteLote());
        assertEquals(35, loteLiberado.getQuantidadeAtual());

        // Verifica gravação no repositório de lote
        verify(loteRepository, times(1)).saveAndFlush(loteLiberado);

        // Verifica gravação da movimentação
        ArgumentCaptor<Movimentacao> movCaptor = ArgumentCaptor.forClass(Movimentacao.class);
        verify(movimentacaoRepository, times(1)).save(movCaptor.capture());
        Movimentacao mov = movCaptor.getValue();
        assertEquals(TipoMovimentacao.BAIXA_SANKHYA_VENDA, mov.getTipoMovimentacao());
        assertEquals(10, mov.getQuantidade());
        assertEquals("NF-105420", mov.getDocumentoReferencia());

        // Verifica log de auditoria via SankhyaAuditLogService
        verify(auditLogService, times(1)).registrarLog(
                eq(105420L), eq("LOT-HER-2026"), eq(10),
                eq("SUCCESS"), eq(200), anyString(), anyString(), anyString()
        );
    }

    @Test
    @DisplayName("Cenário Rejeição: Saldo insuficiente deve lançar InsufficientStockException e auditar OUT_OF_STOCK (409)")
    void deveRejeitarBaixaComSaldoInsuficiente() {
        BaixaEstoqueRequest request = new BaixaEstoqueRequest(
                "MED-ONC-001", "LOT-HER-2026", 50, 105421L,
                "SAIDA_VENDA_HOSPITALAR", "01.234.567/0001-89", "Pedido Excedente", "operador"
        );

        when(loteRepository.findByNumeroLoteComMedicamento("LOT-HER-2026"))
                .thenReturn(Optional.of(loteLiberado)); // saldo disponível = 45

        assertThrows(InsufficientStockException.class, () ->
                sankhyaService.processarBaixaEstoque(request, hoje));

        assertEquals(45, loteLiberado.getQuantidadeAtual(), "O saldo não deve ter sido alterado.");
        verify(movimentacaoRepository, never()).save(any());

        // Verifica se o log de auditoria registrou o erro com código 409
        verify(auditLogService, times(1)).registrarLog(
                eq(105421L), eq("LOT-HER-2026"), eq(50),
                eq("OUT_OF_STOCK"), eq(409), anyString(), anyString(), isNull()
        );
    }

    @Test
    @DisplayName("Cenário Rejeição: Lote vencido deve lançar ExpiredLotException e auditar EXPIRED_LOT_REJECTED (422)")
    void deveRejeitarBaixaEmLoteVencido() {
        loteLiberado.setDataValidade(hoje.minusDays(5)); // Vencido há 5 dias

        BaixaEstoqueRequest request = new BaixaEstoqueRequest(
                "MED-ONC-001", "LOT-HER-2026", 5, 105422L,
                "SAIDA_VENDA_HOSPITALAR", "01.234.567/0001-89", "Pedido", "operador"
        );

        when(loteRepository.findByNumeroLoteComMedicamento("LOT-HER-2026"))
                .thenReturn(Optional.of(loteLiberado));

        assertThrows(ExpiredLotException.class, () ->
                sankhyaService.processarBaixaEstoque(request, hoje));

        verify(movimentacaoRepository, never()).save(any());

        verify(auditLogService, times(1)).registrarLog(
                eq(105422L), eq("LOT-HER-2026"), eq(5),
                eq("EXPIRED_LOT_REJECTED"), eq(422), anyString(), anyString(), isNull()
        );
    }

    @Test
    @DisplayName("Cenário Rejeição: Lote em QUARENTENA deve lançar InvalidLotStatusException e auditar LOT_NOT_RELEASED (422)")
    void deveRejeitarBaixaEmLoteQuarentena() {
        loteLiberado.setStatus(StatusLote.QUARENTENA);

        BaixaEstoqueRequest request = new BaixaEstoqueRequest(
                "MED-ONC-001", "LOT-HER-2026", 5, 105423L,
                "SAIDA_VENDA_HOSPITALAR", "01.234.567/0001-89", "Pedido", "operador"
        );

        when(loteRepository.findByNumeroLoteComMedicamento("LOT-HER-2026"))
                .thenReturn(Optional.of(loteLiberado));

        assertThrows(InvalidLotStatusException.class, () ->
                sankhyaService.processarBaixaEstoque(request, hoje));

        verify(movimentacaoRepository, never()).save(any());

        verify(auditLogService, times(1)).registrarLog(
                eq(105423L), eq("LOT-HER-2026"), eq(5),
                eq("LOT_NOT_RELEASED"), eq(422), anyString(), anyString(), isNull()
        );
    }

    @Test
    @DisplayName("Cenário Rejeição: Lote inexistente deve lançar ResourceNotFoundException e auditar LOT_NOT_FOUND (404)")
    void deveRejeitarLoteInexistente() {
        BaixaEstoqueRequest request = new BaixaEstoqueRequest(
                "MED-ONC-001", "LOT-FANTASMA", 5, 105424L,
                "SAIDA_VENDA", "01.234.567/0001-89", "Pedido", "operador"
        );

        when(loteRepository.findByNumeroLoteComMedicamento("LOT-FANTASMA"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                sankhyaService.processarBaixaEstoque(request, hoje));

        verify(auditLogService, times(1)).registrarLog(
                eq(105424L), eq("LOT-FANTASMA"), eq(5),
                eq("LOT_NOT_FOUND"), eq(404), anyString(), anyString(), isNull()
        );
    }

    @Test
    @DisplayName("Cenário Segurança: Credenciais Sankhya incorretas devem lançar SankhyaAuthException (401)")
    void deveRejeitarCredenciaisInvalidas() {
        assertThrows(SankhyaAuthException.class, () ->
                sankhyaService.validarCredenciaisSankhya("chave-errada", "token-errado"));

        verify(auditLogService, times(1)).registrarLog(
                isNull(), eq("DESCONHECIDO"), eq(0),
                eq("UNAUTHORIZED"), eq(401), anyString(), isNull(), isNull()
        );
    }
}
