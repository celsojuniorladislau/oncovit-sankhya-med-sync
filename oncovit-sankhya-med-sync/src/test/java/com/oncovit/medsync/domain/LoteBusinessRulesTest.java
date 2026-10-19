package com.oncovit.medsync.domain;

import com.oncovit.medsync.domain.enums.CategoriaMedicamento;
import com.oncovit.medsync.domain.enums.StatusLote;
import com.oncovit.medsync.domain.enums.TipoArmazenamento;
import com.oncovit.medsync.domain.model.Lote;
import com.oncovit.medsync.domain.model.Medicamento;
import com.oncovit.medsync.exception.ExpiredLotException;
import com.oncovit.medsync.exception.InsufficientStockException;
import com.oncovit.medsync.exception.InvalidLotStatusException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Regras de Negócio e Ciclo de Vida do Lote (POO)")
class LoteBusinessRulesTest {

    private final LocalDate hoje = LocalDate.of(2026, 10, 19);
    private Medicamento medicamentoTrastuzumabe;

    @BeforeEach
    void setUp() {
        medicamentoTrastuzumabe = new Medicamento(
                "MED-ONC-001",
                "Herceptin 440mg",
                "Trastuzumabe",
                CategoriaMedicamento.ANTICORPO_MONOCLONAL,
                TipoArmazenamento.CADEIA_DE_FRIO,
                new BigDecimal("2.0"),
                new BigDecimal("8.0"),
                "FRASCO_AMPOLA"
        );
    }

    private Lote criarLote(String numero, LocalDate fabricacao, LocalDate validade,
                           StatusLote status, int quantidade, BigDecimal preco) {
        return new Lote(numero, medicamentoTrastuzumabe, fabricacao, validade, status, quantidade, preco, "Roche Farma Brasil");
    }

    @Nested
    @DisplayName("Regra de Validade Crítica (<= 60 dias)")
    class RegraValidadeCritica {

        @Test
        @DisplayName("Deve identificar como crítico lote com exatamente 60 dias para o vencimento")
        void deveSerCriticoNoLimiteExatoDe60Dias() {
            LocalDate validade = hoje.plusDays(60);
            Lote lote = criarLote("LOT-60D", hoje.minusMonths(6), validade, StatusLote.LIBERADO, 10, new BigDecimal("7850.00"));

            assertTrue(lote.isCritico(hoje), "Lote com 60 dias exatos deve ser classificado como crítico.");
            assertEquals(60, lote.getDiasParaVencimento(hoje));
        }

        @Test
        @DisplayName("Deve identificar como crítico lote com 59 dias para o vencimento")
        void deveSerCriticoAbaixoDe60Dias() {
            LocalDate validade = hoje.plusDays(59);
            Lote lote = criarLote("LOT-59D", hoje.minusMonths(6), validade, StatusLote.LIBERADO, 10, new BigDecimal("7850.00"));

            assertTrue(lote.isCritico(hoje));
            assertEquals(59, lote.getDiasParaVencimento(hoje));
        }

        @Test
        @DisplayName("NÃO deve ser crítico quando validade for de 61 dias (acima do limite)")
        void naoDeveSerCriticoAcimaDe60Dias() {
            LocalDate validade = hoje.plusDays(61);
            Lote lote = criarLote("LOT-61D", hoje.minusMonths(6), validade, StatusLote.LIBERADO, 10, new BigDecimal("7850.00"));

            assertFalse(lote.isCritico(hoje), "Lote com 61 dias não deve ser considerado crítico.");
            assertEquals(61, lote.getDiasParaVencimento(hoje));
        }

        @Test
        @DisplayName("NÃO deve ser crítico se o lote estiver em status QUARENTENA mesmo com validade < 60 dias")
        void naoDeveSerCriticoEmQuarentena() {
            LocalDate validade = hoje.plusDays(30);
            Lote lote = criarLote("LOT-QUAR", hoje.minusMonths(6), validade, StatusLote.QUARENTENA, 10, new BigDecimal("7850.00"));

            assertFalse(lote.isCritico(hoje), "Lote em quarentena não pode ter alerta crítico de expedição.");
        }

        @Test
        @DisplayName("NÃO deve ser crítico se o lote estiver BLOQUEADO")
        void naoDeveSerCriticoSeBloqueado() {
            LocalDate validade = hoje.plusDays(15);
            Lote lote = criarLote("LOT-BLOQ", hoje.minusMonths(6), validade, StatusLote.BLOQUEADO, 10, new BigDecimal("7850.00"));

            assertFalse(lote.isCritico(hoje));
        }

        @Test
        @DisplayName("NÃO deve ser crítico se a quantidade atual for 0 (esgotado)")
        void naoDeveSerCriticoComSaldoZero() {
            LocalDate validade = hoje.plusDays(10);
            Lote lote = criarLote("LOT-ZERADO", hoje.minusMonths(6), validade, StatusLote.LIBERADO, 0, new BigDecimal("7850.00"));

            assertFalse(lote.isCritico(hoje), "Lote com saldo zerado não gera alerta crítico de perda.");
        }

        @Test
        @DisplayName("NÃO deve ser crítico se já estiver vencido")
        void naoDeveSerCriticoSeJaVencido() {
            LocalDate validade = hoje.minusDays(1);
            Lote lote = criarLote("LOT-VENC", hoje.minusMonths(12), validade, StatusLote.LIBERADO, 10, new BigDecimal("7850.00"));

            assertTrue(lote.isVencido(hoje));
            assertFalse(lote.isCritico(hoje), "Lote já vencido não é iminente, é vencido.");
        }
    }

    @Nested
    @DisplayName("Regras de Débito de Estoque e Validações Sanitárias")
    class RegrasDebitoEstoque {

        @Test
        @DisplayName("Deve debitar com sucesso em lote liberado com saldo suficiente")
        void deveDebitarComSucesso() {
            Lote lote = criarLote("LOT-OK", hoje.minusMonths(3), hoje.plusMonths(12), StatusLote.LIBERADO, 50, new BigDecimal("7850.00"));

            lote.debitarEstoque(20, hoje);

            assertEquals(30, lote.getQuantidadeAtual());
        }

        @Test
        @DisplayName("Deve permitir débito exato até zerar o estoque")
        void devePermitirDebitoAteZerar() {
            Lote lote = criarLote("LOT-ZERAR", hoje.minusMonths(3), hoje.plusMonths(12), StatusLote.LIBERADO, 15, new BigDecimal("7850.00"));

            lote.debitarEstoque(15, hoje);

            assertEquals(0, lote.getQuantidadeAtual());
        }

        @Test
        @DisplayName("Deve lançar InsufficientStockException quando quantidade for maior que saldo")
        void deveLancarExcecaoSaldoInsuficiente() {
            Lote lote = criarLote("LOT-SEM-SALDO", hoje.minusMonths(3), hoje.plusMonths(12), StatusLote.LIBERADO, 10, new BigDecimal("7850.00"));

            InsufficientStockException ex = assertThrows(InsufficientStockException.class, () ->
                    lote.debitarEstoque(15, hoje));

            assertEquals(10, lote.getQuantidadeAtual(), "O saldo deve permanecer intacto.");
            assertTrue(ex.getMessage().contains("Saldo insuficiente"));
        }

        @Test
        @DisplayName("Deve lançar ExpiredLotException ao tentar debitar lote vencido")
        void deveLancarExcecaoLoteVencido() {
            LocalDate ontem = hoje.minusDays(1);
            Lote lote = criarLote("LOT-VENCIDO", hoje.minusMonths(12), ontem, StatusLote.LIBERADO, 25, new BigDecimal("7850.00"));

            ExpiredLotException ex = assertThrows(ExpiredLotException.class, () ->
                    lote.debitarEstoque(5, hoje));

            assertEquals(25, lote.getQuantidadeAtual());
            assertEquals(StatusLote.VENCIDO, lote.getStatus());
            assertTrue(ex.getMessage().contains("barreira sanitária"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"QUARENTENA", "BLOQUEADO", "REJEITADO", "VENCIDO"})
        @DisplayName("Deve lançar InvalidLotStatusException para qualquer status diferente de LIBERADO")
        void deveRejeitarStatusNaoLiberados(String statusNome) {
            StatusLote status = StatusLote.valueOf(statusNome);
            Lote lote = criarLote("LOT-TESTE", hoje.minusMonths(2), hoje.plusMonths(10), status, 30, new BigDecimal("7850.00"));

            InvalidLotStatusException ex = assertThrows(InvalidLotStatusException.class, () ->
                    lote.debitarEstoque(5, hoje));

            assertEquals(30, lote.getQuantidadeAtual());
            assertTrue(ex.getMessage().contains("não pode ser dispensado"));
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1, -50})
        @DisplayName("Deve rejeitar débito com quantidade zero ou negativa")
        void deveRejeitarQuantidadeZeroOuNegativa(int qtdInvalida) {
            Lote lote = criarLote("LOT-QTD", hoje.minusMonths(2), hoje.plusMonths(10), StatusLote.LIBERADO, 30, new BigDecimal("7850.00"));

            assertThrows(IllegalArgumentException.class, () ->
                    lote.debitarEstoque(qtdInvalida, hoje));
        }
    }

    @Nested
    @DisplayName("Cálculos Financeiros e Métricas")
    class MetricasFinanceiras {

        @Test
        @DisplayName("Deve calcular valor financeiro total do lote corretamente")
        void deveCalcularValorTotalLote() {
            Lote lote = criarLote("LOT-FIN", hoje.minusMonths(2), hoje.plusMonths(10), StatusLote.LIBERADO, 20, new BigDecimal("7850.00"));

            BigDecimal total = lote.getValorFinanceiroTotal();

            assertEquals(new BigDecimal("157000.00"), total);
        }
    }
}
