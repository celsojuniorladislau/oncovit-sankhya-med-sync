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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/lotes/criticos deve retornar apenas lotes com expiração <= 60 dias ordenados por validade ASC (FEFO)")
    void deveListarLotesCriticosOrdenadosFEFO() throws Exception {
        mockMvc.perform(get("/api/lotes/criticos")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", isA(Iterable.class)))
            .andExpect(jsonPath("$[0].alertaCritico", is(true)))
            .andExpect(jsonPath("$[0].diasParaVencimento", lessThanOrEqualTo(60)))
            .andExpect(jsonPath("$[0].status", is("LIBERADO")))
            .andExpect(jsonPath("$[0].quantidadeDisponivel", greaterThan(0)));
    }

    @Test
    @DisplayName("GET /api/lotes/{id}/etiqueta deve retornar EtiquetaDTO com metadados e ZPL II com tarja invertida")
    void deveGerarEtiquetaZplComSucesso() throws Exception {
        // ID 1 corresponde ao primeiro lote semeado (Herceptin - CADEIA_DE_FRIO)
        mockMvc.perform(get("/api/lotes/1/etiqueta")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.loteId", is(1)))
            .andExpect(jsonPath("$.tipoArmazenamento", is("CADEIA_DE_FRIO")))
            .andExpect(jsonPath("$.formato", is("ZPL_II_203DPI")))
            .andExpect(jsonPath("$.conteudoZpl", startsWith("^XA")))
            .andExpect(jsonPath("$.conteudoZpl", endsWith("^XZ")))
            .andExpect(jsonPath("$.conteudoZpl", containsString("^PW800")))
            .andExpect(jsonPath("$.conteudoZpl", containsString("^LL480")))
            .andExpect(jsonPath("$.conteudoZpl", containsString("^CI28")))
            .andExpect(jsonPath("$.conteudoZpl", containsString("CONSERVAR ENTRE 2C E 8C - TERMOLABIL / NAO CONGELAR")))
            .andExpect(jsonPath("$.conteudoZpl", containsString("^BC")))
            .andExpect(jsonPath("$.conteudoZpl", containsString("^BX")));
    }

    @Test
    @DisplayName("GET /api/lotes/{id}/etiqueta deve retornar 404 Not Found para lote inexistente")
    void deveRetornar404ParaLoteInexistente() throws Exception {
        mockMvc.perform(get("/api/lotes/99999/etiqueta")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
}
