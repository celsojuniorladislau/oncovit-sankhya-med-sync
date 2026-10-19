package com.oncovit.medsync.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Payload enviado pelo Sankhya ERP para solicitar a baixa de estoque de um lote")
public record BaixaEstoqueRequest(
    @Schema(description = "Código do produto no Sankhya ou SKU Oncovit", example = "MED-ONC-001")
    @JsonAlias({"codProd", "codigoProduto"})
    String codigoSankhyaProduto,

    @Schema(description = "Identificador de lote (CONTROLE no Sankhya)", example = "LOT-TRA-2026A", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O número do lote é obrigatório.")
    @JsonAlias({"controle", "lote"})
    String numeroLote,

    @Schema(description = "Quantidade a ser baixada (deve ser maior que zero)", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "A quantidade é obrigatória.")
    @Positive(message = "A quantidade deve ser estritamente maior que zero.")
    @JsonAlias({"qtdNeg", "qtd"})
    Integer quantidade,

    @Schema(description = "Número único da nota fiscal (NUNOTA Sankhya - opcional para baixas operacionais avulsas)", example = "105420")
    @JsonAlias({"nuNota", "notaFiscal", "numeroNota"})
    Long numeroNota,

    @Schema(description = "Tipo de operação de saída", example = "SAIDA_VENDA_HOSPITALAR")
    @JsonAlias({"codTipOper", "tipoMovimentacao"})
    String tipoOperacao,

    @Schema(description = "CNPJ do hospital ou cliente de destino", example = "01.234.567/0001-89")
    String cnpjCliente,

    @Schema(description = "Motivo descritivo da baixa / Justificativa operacional", example = "Atendimento pedido hospitalar Oncovit")
    String motivo,

    @Schema(description = "Identificação do usuário ou rotina operadora no Sankhya", example = "sankhya.operador")
    String usuario
) {}
