package com.oncovit.medsync.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Estrutura padronizada para retorno de erros de negócio, validação e segurança")
public record ErrorResponseDTO(
    @Schema(description = "Código textual do status do erro", example = "EXPIRED_LOT_REJECTED")
    String status,

    @Schema(description = "Identificador de erro de domínio", example = "EXPIRED_LOT_REJECTED")
    String erro,

    @Schema(description = "Código de erro padronizado", example = "EXPIRED_LOT_REJECTED")
    String codigoErro,

    @Schema(description = "Código HTTP numérico", example = "422")
    int statusCode,

    @Schema(description = "Mensagem detalhada do erro", example = "Operação abortada por barreira sanitária: O lote está vencido e não pode ser dispensado.")
    String mensagem,

    @Schema(description = "URI da requisição", example = "/api/integracao/sankhya/baixa-estoque")
    String path,

    @Schema(description = "Timestamp ISO-8601 do evento", example = "2026-10-19T18:55:00")
    LocalDateTime timestamp
) {
    /**
     * Construtor de compatibilidade para código existente em GlobalExceptionHandler.
     * Mapeia automaticamente o código textual de erro para os campos status, erro e codigoErro.
     */
    public ErrorResponseDTO(int statusCode, String erro, String mensagem, String path, LocalDateTime timestamp) {
        this(erro, erro, erro, statusCode, mensagem, path, timestamp != null ? timestamp : LocalDateTime.now());
    }

    public ErrorResponseDTO(int statusCode, String erro, String mensagem, String path) {
        this(statusCode, erro, mensagem, path, LocalDateTime.now());
    }

    // Getters auxiliares para serialização e compatibilidade
    public String getError() {
        return erro;
    }

    public String getMessage() {
        return mensagem;
    }

    public int getStatusHttp() {
        return statusCode;
    }

    public String getCodigoErro() {
        return codigoErro;
    }
}
