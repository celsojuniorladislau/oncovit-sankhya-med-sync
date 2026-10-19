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
public class MedicamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/medicamentos deve retornar lista com status 200 e estoque disponível calculado")
    void deveListarMedicamentosComSucesso() throws Exception {
        mockMvc.perform(get("/api/medicamentos")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", isA(Iterable.class)))
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(8))))
            .andExpect(jsonPath("$[0].id").exists())
            .andExpect(jsonPath("$[0].codigoSankhya").exists())
            .andExpect(jsonPath("$[0].nomeComercial").exists())
            .andExpect(jsonPath("$[0].tipoArmazenamento", anyOf(is("CADEIA_DE_FRIO"), is("AMBIENTE"))))
            .andExpect(jsonPath("$[0].totalEstoqueDisponivel", greaterThanOrEqualTo(0)));
    }
}
