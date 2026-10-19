package com.oncovit.medsync.domain.enums;

import java.math.BigDecimal;

public enum TipoArmazenamento {
    CADEIA_DE_FRIO(
            "Cadeia de Frio (Termolábil)",
            new BigDecimal("2.0"),
            new BigDecimal("8.0"),
            "CONSERVAR ENTRE 2C E 8C - TERMOLABIL / NAO CONGELAR"
    ),
    AMBIENTE(
            "Ambiente Controlado (Termoestável)",
            new BigDecimal("15.0"),
            new BigDecimal("25.0"),
            "CONSERVAR ENTRE 15C E 25C - TEMPERATURA AMBIENTE"
    );

    private final String descricao;
    private final BigDecimal temperaturaMinimaPadrao;
    private final BigDecimal temperaturaMaximaPadrao;
    private final String textoTarjaEtiqueta;

    TipoArmazenamento(String descricao, BigDecimal tempMin, BigDecimal tempMax, String textoTarjaEtiqueta) {
        this.descricao = descricao;
        this.temperaturaMinimaPadrao = tempMin;
        this.temperaturaMaximaPadrao = tempMax;
        this.textoTarjaEtiqueta = textoTarjaEtiqueta;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getTemperaturaMinimaPadrao() {
        return temperaturaMinimaPadrao;
    }

    public BigDecimal getTemperaturaMaximaPadrao() {
        return temperaturaMaximaPadrao;
    }

    public String getTextoTarjaEtiqueta() {
        return textoTarjaEtiqueta;
    }

    public boolean isCadeiaDeFrio() {
        return this == CADEIA_DE_FRIO;
    }
}
