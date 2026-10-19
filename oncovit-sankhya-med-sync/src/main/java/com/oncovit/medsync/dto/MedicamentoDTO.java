package com.oncovit.medsync.dto;

import com.oncovit.medsync.domain.enums.CategoriaMedicamento;
import com.oncovit.medsync.domain.enums.TipoArmazenamento;
import com.oncovit.medsync.domain.model.Medicamento;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Representação detalhada do medicamento e seu estoque disponível")
public record MedicamentoDTO(
    @Schema(description = "Identificador único do medicamento", example = "1")
    Long id,

    @Schema(description = "Código SKU / Código de integração Sankhya", example = "MED-ONC-001")
    String codigoSankhya,

    @Schema(description = "Nome comercial do produto", example = "Herceptin 440mg")
    String nomeComercial,

    @Schema(description = "Denominação Comum Brasileira (DCB) / Princípio ativo", example = "Trastuzumabe")
    String principioAtivo,

    @Schema(description = "Dosagem e concentração", example = "440 mg")
    String dosagem,

    @Schema(description = "Forma farmacêutica", example = "Pó liofilizado para solução injetável")
    String formaFarmaceutica,

    @Schema(description = "Categoria terapêutica", example = "ANTICORPO_MONOCLONAL")
    String categoria,

    @Schema(description = "Classe térmica de armazenamento", example = "CADEIA_DE_FRIO")
    TipoArmazenamento tipoArmazenamento,

    @Schema(description = "Temperatura mínima recomendada (°C)", example = "2.0")
    BigDecimal temperaturaMinima,

    @Schema(description = "Temperatura máxima recomendada (°C)", example = "8.0")
    BigDecimal temperaturaMaxima,

    @Schema(description = "Unidade de medida padrão para expedição", example = "FRASCO_AMPOLA")
    String unidadeMedida,

    @Schema(description = "Número do registro na ANVISA", example = "1.0107.0123.001-2")
    String registroAnvisa,

    @Schema(description = "Quantidade física total disponível somando lotes liberados e não vencidos", example = "85")
    int totalEstoqueDisponivel
) {
    public static MedicamentoDTO fromEntity(Medicamento m) {
        return new MedicamentoDTO(
                m.getId(),
                m.getCodigoSankhya(),
                m.getNomeComercial(),
                m.getPrincipioAtivo(),
                m.getDosagem(),
                m.getFormaFarmaceutica(),
                m.getCategoria() != null ? m.getCategoria().name() : null,
                m.getTipoArmazenamento(),
                m.getTemperaturaMinima(),
                m.getTemperaturaMaxima(),
                m.getUnidadeMedida(),
                m.getRegistroAnvisa(),
                m.getTotalEstoqueDisponivel()
        );
    }
}
