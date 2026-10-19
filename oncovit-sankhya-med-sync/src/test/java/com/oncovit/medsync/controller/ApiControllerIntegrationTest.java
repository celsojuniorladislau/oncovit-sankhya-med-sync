package com.oncovit.medsync.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes de Integração End-to-End da API REST Oncovit Med Sync")
public class ApiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String APP_KEY = "oncovit-medsync-api-key-2026";
    private static final String TOKEN = "bearer sankhya-session-jwt-token-example";

    @Test
    @DisplayName("Cenário 1: Listagem de medicamentos deve retornar catálogo completo com HTTP 200")
    void deveListarCatalogoMedicamentos() throws Exception {
        mockMvc.perform(get("/api/medicamentos")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(8))))
            .andExpect(jsonPath("$[0].codigoSankhya", notNullValue()))
            .andExpect(jsonPath("$[0].nomeComercial", notNullValue()))
            .andExpect(jsonPath("$[0].totalEstoqueDisponivel", greaterThanOrEqualTo(0)));
    }

    @Test
    @DisplayName("Cenário 2: Consulta de lotes críticos deve retornar apenas lotes com validade <= 60 dias (FEFO)")
    void deveRetornarLotesCriticosFEFO() throws Exception {
        mockMvc.perform(get("/api/lotes/criticos")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", isA(Iterable.class)))
            .andExpect(jsonPath("$[0].alertaCritico", is(true)))
            .andExpect(jsonPath("$[0].diasParaVencimento", lessThanOrEqualTo(60)));
    }

    @Test
    @DisplayName("Cenário 3: Geração de etiqueta térmica ZPL II e JSON para lote com cadeia de frio")
    void deveGerarEtiquetaTermicaCadeiaDeFrio() throws Exception {
        mockMvc.perform(get("/api/lotes/1/etiqueta")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.loteId", is(1)))
            .andExpect(jsonPath("$.formato", is("ZPL_II_203DPI")))
            .andExpect(jsonPath("$.conteudoZpl", startsWith("^XA")))
            .andExpect(jsonPath("$.conteudoZpl", containsString("^GB740,50,50")))
            .andExpect(jsonPath("$.conteudoZpl", containsString("CONSERVAR ENTRE 2C E 8C")))
            .andExpect(jsonPath("$.conteudoZpl", endsWith("^XZ")));
    }

    @Test
    @DisplayName("Cenário 4: Webhook Sankhya ERP deve efetuar baixa de estoque atômica com sucesso")
    void deveEfetuarBaixaSankhyaComSucesso() throws Exception {
        String payload = """
            {
              "codigoSankhyaProduto": "MED-SUP-005",
              "numeroLote": "LOT-FIL-2024-01",
              "quantidade": 10,
              "numeroNota": 105430,
              "tipoOperacao": "SAIDA_VENDA_HOSPITALAR",
              "cnpjCliente": "01.234.567/0001-89",
              "motivo": "Atendimento pedido hospitalar Oncovit"
            }
            """;

        mockMvc.perform(post("/api/integracao/sankhya/baixa-estoque")
                .header("X-Sankhya-AppKey", APP_KEY)
                .header("X-Sankhya-Token", TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("SUCCESS")))
            .andExpect(jsonPath("$.protocolo", startsWith("SNK-")))
            .andExpect(jsonPath("$.quantidadeBaixada", is(10)));
    }

    @Test
    @DisplayName("Cenário 5: Webhook Sankhya deve rejeitar requisição sem cabeçalho de autenticação com HTTP 401")
    void deveRejeitarSemAutenticacao() throws Exception {
        String payload = """
            {
              "numeroLote": "LOT-FIL-2024-01",
              "quantidade": 5,
              "numeroNota": 105431
            }
            """;

        mockMvc.perform(post("/api/integracao/sankhya/baixa-estoque")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.erro", is("UNAUTHORIZED")));
    }

    @Test
    @DisplayName("Cenário 6: Webhook Sankhya deve rejeitar baixa com saldo insuficiente com HTTP 409")
    void deveRejeitarSaldoInsuficiente() throws Exception {
        String payload = """
            {
              "numeroLote": "LOT-FIL-2024-01",
              "quantidade": 999999,
              "numeroNota": 105432
            }
            """;

        mockMvc.perform(post("/api/integracao/sankhya/baixa-estoque")
                .header("X-Sankhya-AppKey", APP_KEY)
                .header("X-Sankhya-Token", TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.erro", is("OUT_OF_STOCK")));
    }

    @Test
    @DisplayName("Cenário 7: Webhook Sankhya deve rejeitar baixa em lote bloqueado/vencido com HTTP 422")
    void deveRejeitarLoteBloqueado() throws Exception {
        String payload = """
            {
              "numeroLote": "LOT-MAB-2023-EX",
              "quantidade": 1,
              "numeroNota": 105433
            }
            """;

        mockMvc.perform(post("/api/integracao/sankhya/baixa-estoque")
                .header("X-Sankhya-AppKey", APP_KEY)
                .header("X-Sankhya-Token", TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.erro", anyOf(is("EXPIRED_LOT_REJECTED"), is("LOT_NOT_RELEASED"), is("INVALID_STATUS"))));
    }
}
