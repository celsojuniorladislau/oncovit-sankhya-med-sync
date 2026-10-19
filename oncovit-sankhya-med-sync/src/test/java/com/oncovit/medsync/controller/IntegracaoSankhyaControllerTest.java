package com.oncovit.medsync.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegracaoSankhyaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String APP_KEY = "oncovit-medsync-api-key-2026";
    private static final String TOKEN = "bearer sankhya-session-jwt-token-example";

    @Test
    @DisplayName("POST baixa-estoque deve retornar 200 OK e reduzir saldo do lote para transacao valida")
    void deveProcessarBaixaComSucesso() throws Exception {
        String payload = """
            {
              "codigoSankhyaProduto": "MED-ONC-001",
              "numeroLote": "LOT-HER-2024-02",
              "quantidade": 5,
              "numeroNota": 105420,
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
            .andExpect(jsonPath("$.saldoRestanteLote", is(115))); // 120 - 5 = 115
    }

    @Test
    @DisplayName("POST baixa-estoque deve retornar 401 Unauthorized se cabeçalhos Sankhya faltarem")
    void deveRetornar401SeCabecalhosFaltarem() throws Exception {
        String payload = """
            {
              "numeroLote": "LOT-HER-2024-02",
              "quantidade": 5,
              "numeroNota": 105421
            }
            """;

        mockMvc.perform(post("/api/integracao/sankhya/baixa-estoque")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.erro", is("UNAUTHORIZED")));
    }

    @Test
    @DisplayName("POST baixa-estoque deve retornar 400 Bad Request se quantidade for zero ou negativa")
    void deveRetornar400ParaQuantidadeInvalida() throws Exception {
        String payload = """
            {
              "numeroLote": "LOT-HER-2024-02",
              "quantidade": -1,
              "numeroNota": 105422
            }
            """;

        mockMvc.perform(post("/api/integracao/sankhya/baixa-estoque")
                .header("X-Sankhya-AppKey", APP_KEY)
                .header("X-Sankhya-Token", TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.erro", is("VALIDATION_FAILED")));
    }

    @Test
    @DisplayName("POST baixa-estoque deve retornar 404 Not Found se o lote nao existir")
    void deveRetornar404ParaLoteInexistente() throws Exception {
        String payload = """
            {
              "numeroLote": "LOTE-INEXISTENTE-999",
              "quantidade": 2,
              "numeroNota": 105423
            }
            """;

        mockMvc.perform(post("/api/integracao/sankhya/baixa-estoque")
                .header("X-Sankhya-AppKey", APP_KEY)
                .header("X-Sankhya-Token", TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro", is("LOT_NOT_FOUND")));
    }

    @Test
    @DisplayName("POST baixa-estoque deve retornar 409 Conflict OUT_OF_STOCK se saldo for insuficiente")
    void deveRetornar409ParaSaldoInsuficiente() throws Exception {
        String payload = """
            {
              "numeroLote": "LOT-MAB-2024-01",
              "quantidade": 9999,
              "numeroNota": 105424
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
    @DisplayName("POST baixa-estoque deve retornar 422 Unprocessable para lote vencido ou bloqueado")
    void deveRetornar422ParaLoteVencidoOuBloqueado() throws Exception {
        // Lote LOT-MAB-2023-EX possui status BLOQUEADO
        String payload = """
            {
              "numeroLote": "LOT-MAB-2023-EX",
              "quantidade": 1,
              "numeroNota": 105425
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
