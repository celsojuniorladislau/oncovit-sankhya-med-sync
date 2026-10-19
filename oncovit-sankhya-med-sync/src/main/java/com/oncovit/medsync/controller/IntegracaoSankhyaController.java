package com.oncovit.medsync.controller;

import com.oncovit.medsync.dto.BaixaEstoqueRequest;
import com.oncovit.medsync.dto.BaixaEstoqueResponse;
import com.oncovit.medsync.dto.ErrorResponseDTO;
import com.oncovit.medsync.service.SankhyaIntegracaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/integracao/sankhya")
@Tag(name = "Integração Sankhya ERP", description = "Endpoints de webhook simulando sincronização de inventário e faturamento do ERP Sankhya")
public class IntegracaoSankhyaController {

    private final SankhyaIntegracaoService sankhyaIntegracaoService;

    public IntegracaoSankhyaController(SankhyaIntegracaoService sankhyaIntegracaoService) {
        this.sankhyaIntegracaoService = sankhyaIntegracaoService;
    }

    @PostMapping("/baixa-estoque")
    @Operation(
        summary = "Webhook de baixa de estoque Sankhya ERP",
        description = "Simula a recepção de baixa de estoque disparada pela confirmação de nota fiscal (TGFCAB/TGFITE) no ERP Sankhya. Valida barreiras sanitárias, autenticação e executa a dedução atômica no estoque com registro no log de sincronização."
    )
    @Parameter(
        name = "X-Sankhya-AppKey",
        in = ParameterIn.HEADER,
        required = true,
        description = "Chave de aplicação do parceiro Sankhya",
        schema = @Schema(type = "string", defaultValue = "oncovit-medsync-api-key-2026")
    )
    @Parameter(
        name = "X-Sankhya-Token",
        in = ParameterIn.HEADER,
        required = true,
        description = "Token de autorização da sessão Sankhya",
        schema = @Schema(type = "string", defaultValue = "bearer sankhya-session-jwt-token-example")
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Baixa efetuada com sucesso",
            content = @Content(schema = @Schema(implementation = BaixaEstoqueResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Requisição malformada ou dados inválidos (ex: quantidade <= 0)",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Não autorizado (cabeçalhos de autenticação Sankhya ausentes ou inválidos)",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Medicamento ou Lote não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Saldo insuficiente no lote (OUT_OF_STOCK)",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "422",
            description = "Rejeitado por barreira sanitária: Lote vencido (EXPIRED_LOT_REJECTED) ou status inválido",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
        )
    })
    public ResponseEntity<BaixaEstoqueResponse> realizarBaixaEstoque(
        @RequestHeader(value = "X-Sankhya-AppKey", required = false) String appKey,
        @RequestHeader(value = "X-Sankhya-Token", required = false) String token,
        @Valid @RequestBody BaixaEstoqueRequest request
    ) {
        BaixaEstoqueResponse response = sankhyaIntegracaoService.processarBaixa(appKey, token, request);
        return ResponseEntity.ok(response);
    }
}
