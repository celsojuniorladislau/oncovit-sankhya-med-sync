package com.oncovit.medsync.controller;

import com.oncovit.medsync.dto.MedicamentoDTO;
import com.oncovit.medsync.service.MedicamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/medicamentos")
@Tag(name = "Medicamentos", description = "Catálogo de medicamentos oncológicos e hospitalares de alta complexidade")
public class MedicamentoController {

    private final MedicamentoService medicamentoService;

    public MedicamentoController(MedicamentoService medicamentoService) {
        this.medicamentoService = medicamentoService;
    }

    @GetMapping
    @Operation(
        summary = "Listar catálogo de medicamentos",
        description = "Retorna todos os medicamentos cadastrados com suas faixas térmicas de conservação e o saldo total disponível calculado a partir dos lotes liberados e não vencidos."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de medicamentos retornada com sucesso",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = MedicamentoDTO.class)))
    )
    public ResponseEntity<List<MedicamentoDTO>> listarTodos() {
        return ResponseEntity.ok(medicamentoService.listarTodosComEstoqueDisponivel());
    }
}
