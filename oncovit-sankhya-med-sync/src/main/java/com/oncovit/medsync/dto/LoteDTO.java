package com.oncovit.medsync.dto;

import com.oncovit.medsync.domain.enums.StatusLote;
import com.oncovit.medsync.domain.enums.TipoArmazenamento;
import com.oncovit.medsync.domain.model.Lote;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Dados do lote farmacêutico com indicadores de validade e risco financeiro")
public record LoteDTO(
    @Schema(description = "Identificador do lote", example = "10")
    Long id,

    @Schema(description = "Número oficial de controle do lote", example = "LOT-TRA-2026A")
    String numeroLote,

    @Schema(description = "ID do medicamento associado", example = "1")
    Long medicamentoId,

    @Schema(description = "Código de integração Sankhya do medicamento", example = "MED-ONC-001")
    String codigoSankhyaMedicamento,

    @Schema(description = "Nome comercial e princípio ativo do medicamento", example = "Herceptin 440mg (Trastuzumabe)")
    String nomeMedicamento,

    @Schema(description = "Princípio ativo do medicamento", example = "Trastuzumabe")
    String principioAtivo,

    @Schema(description = "Data de fabricação", example = "2025-11-15")
    LocalDate dataFabricacao,

    @Schema(description = "Data de validade", example = "2026-11-15")
    LocalDate dataValidade,

    @Schema(description = "Quantidade de dias restantes até a expiração", example = "27")
    long diasParaVencimento,

    @Schema(description = "Status atual do lote", example = "LIBERADO")
    StatusLote status,

    @Schema(description = "Quantidade atual disponível para faturamento", example = "30")
    int quantidadeDisponivel,

    @Schema(description = "Preço unitário do lote", example = "7850.00")
    BigDecimal precoUnitario,

    @Schema(description = "Valor financeiro total em estoque do lote", example = "235500.00")
    BigDecimal valorFinanceiroTotal,

    @Schema(description = "Classe térmica de armazenamento", example = "CADEIA_DE_FRIO")
    TipoArmazenamento tipoArmazenamento,

    @Schema(description = "Flag indicativa de shelf-life crítico (<= 60 dias)", example = "true")
    boolean alertaCritico
) {
    public static LoteDTO fromEntity(Lote lote) {
        return fromEntity(lote, LocalDate.now());
    }

    public static LoteDTO fromEntity(Lote lote, LocalDate dataReferencia) {
        String nomeCompleto = lote.getMedicamento() != null ? 
                lote.getMedicamento().getNomeComercial() + " (" + lote.getMedicamento().getPrincipioAtivo() + ")" : "";
        String codigo = lote.getMedicamento() != null ? lote.getMedicamento().getCodigoSankhya() : "";
        Long medId = lote.getMedicamento() != null ? lote.getMedicamento().getId() : null;
        TipoArmazenamento arm = lote.getMedicamento() != null ? lote.getMedicamento().getTipoArmazenamento() : null;
        String principio = lote.getMedicamento() != null ? lote.getMedicamento().getPrincipioAtivo() : "";

        return new LoteDTO(
                lote.getId(),
                lote.getNumeroLote(),
                medId,
                codigo,
                nomeCompleto,
                principio,
                lote.getDataFabricacao(),
                lote.getDataValidade(),
                lote.getDiasParaVencimento(dataReferencia),
                lote.getStatus(),
                lote.getQuantidadeAtual() != null ? lote.getQuantidadeAtual() : 0,
                lote.getPrecoUnitario(),
                lote.getValorFinanceiroTotal(),
                arm,
                lote.isCritico(dataReferencia)
        );
    }
}
