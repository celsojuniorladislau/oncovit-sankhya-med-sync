package com.oncovit.medsync.controller;

import com.oncovit.medsync.dto.EtiquetaDTO;
import com.oncovit.medsync.dto.LoteDTO;
import com.oncovit.medsync.service.EtiquetaZplService;
import com.oncovit.medsync.service.LoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lotes")
@Tag(name = "Lotes", description = "Operações com lotes farmacêuticos, auditoria FEFO e etiquetagem térmica")
public class LoteController {

    private final LoteService loteService;
    private final EtiquetaZplService etiquetaZplService;

    public LoteController(LoteService loteService, EtiquetaZplService etiquetaZplService) {
        this.loteService = loteService;
        this.etiquetaZplService = etiquetaZplService;
    }

    @GetMapping("/criticos")
    @Operation(
        summary = "Listar lotes com validade crítica (< 60 dias) - FEFO",
        description = "Retorna os lotes ativos em status LIBERADO com data de validade nos próximos 60 dias a partir da data atual, ordenados crescentemente por data de validade (First Expired, First Out) para priorização logística e prevenção de perdas."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de lotes críticos retornada com sucesso",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = LoteDTO.class)))
    )
    public ResponseEntity<List<LoteDTO>> listarLotesCriticos() {
        return ResponseEntity.ok(loteService.listarLotesCriticos());
    }

    @GetMapping("/{id}/etiqueta")
    @Operation(
        summary = "Gerar etiqueta térmica de expedição (JSON + ZPL II)",
        description = "Gera a carga de dados para a etiqueta de expedição contendo tanto os metadados JSON estruturados quanto a string no formato Zebra Programming Language (ZPL II) calibrada para 203 DPI (100x60mm) com destaque de cadeia de frio e códigos GS1."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Etiqueta gerada com sucesso",
            content = @Content(schema = @Schema(implementation = EtiquetaDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Lote não encontrado para o ID especificado"
        )
    })
    public ResponseEntity<EtiquetaDTO> gerarEtiqueta(
        @Parameter(description = "ID do lote farmacêutico", required = true, example = "1")
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(etiquetaZplService.gerarEtiquetaPorLoteId(id));
    }
}
