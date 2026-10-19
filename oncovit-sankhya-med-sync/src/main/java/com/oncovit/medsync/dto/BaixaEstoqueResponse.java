package com.oncovit.medsync.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Resposta de confirmação da transação de baixa de estoque")
public record BaixaEstoqueResponse(
    @Schema(description = "Status consolidado da transação", example = "SUCCESS")
    String status,

    @Schema(description = "Protocolo de auditoria gerado pelo sistema", example = "SNK-20261019-89421")
    String protocolo,

    @Schema(description = "Mensagem explicativa do processamento", example = "Baixa de estoque confirmada com sucesso no Oncovit Med Sync.")
    String mensagem,

    @Schema(description = "Quantidade efetivamente baixada do estoque", example = "5")
    Integer quantidadeBaixada,

    @Schema(description = "Saldo anterior do lote antes da dedução", example = "30")
    Integer saldoAnterior,

    @Schema(description = "Saldo físico restante do lote após a dedução", example = "25")
    Integer saldoRestanteLote,

    @Schema(description = "Carimbo de data/hora do processamento", example = "2026-10-19T18:55:00")
    LocalDateTime dataHoraProcessamento
) {
    public BaixaEstoqueResponse(String status, String protocolo, String mensagem, Integer saldoRestanteLote, LocalDateTime dataHoraProcessamento) {
        this(status, protocolo, mensagem, null, null, saldoRestanteLote, dataHoraProcessamento);
    }
}
