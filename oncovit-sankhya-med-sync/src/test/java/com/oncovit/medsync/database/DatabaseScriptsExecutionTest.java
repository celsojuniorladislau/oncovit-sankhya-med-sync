package com.oncovit.medsync.database;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DatabaseScriptsExecutionTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Verifica se os dados de semente foram carregados e se Query 1 (validade < 60d) retorna lotes criticos")
    void deveExecutarQueryValidadeCriticaComSucesso() {
        String sqlQuery1 = """
            SELECT 
                l.numero_lote,
                m.nome_comercial,
                m.tipo_armazenamento,
                l.quantidade_atual,
                l.preco_unitario,
                ROUND(l.quantidade_atual * l.preco_unitario, 2) AS valor_financeiro_risco,
                l.data_validade
            FROM lotes l
            JOIN medicamentos m ON l.medicamento_id = m.id
            WHERE l.quantidade_atual > 0
              AND l.status_lote = 'LIBERADO'
              AND l.data_validade <= (CURRENT_DATE + 60)
            ORDER BY l.data_validade ASC
            """;

        List<Map<String, Object>> resultados = jdbcTemplate.queryForList(sqlQuery1);
        assertThat(resultados).isNotEmpty();
        assertThat(resultados.get(0).get("NUMERO_LOTE")).isNotNull();
    }

    @Test
    @DisplayName("Verifica se Query 2 (agrupamento por armazenamento) totaliza Cadeia de Frio e Ambiente")
    void deveExecutarQueryAgrupamentoArmazenamentoComSucesso() {
        String sqlQuery2 = """
            SELECT 
                m.tipo_armazenamento,
                COUNT(DISTINCT m.id) AS total_medicamentos,
                SUM(l.quantidade_atual) AS saldo_total,
                ROUND(SUM(l.quantidade_atual * l.preco_unitario), 2) AS valor_total
            FROM medicamentos m
            JOIN lotes l ON l.medicamento_id = m.id
            WHERE l.quantidade_atual > 0
            GROUP BY m.tipo_armazenamento
            ORDER BY m.tipo_armazenamento
            """;

        List<Map<String, Object>> resultados = jdbcTemplate.queryForList(sqlQuery2);
        assertThat(resultados).hasSize(2);
        assertThat(resultados.stream().map(r -> r.get("TIPO_ARMAZENAMENTO").toString()))
            .containsExactlyInAnyOrder("AMBIENTE", "CADEIA_DE_FRIO");
    }
}
