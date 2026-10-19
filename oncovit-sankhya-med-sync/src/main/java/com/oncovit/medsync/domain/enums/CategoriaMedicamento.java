package com.oncovit.medsync.domain.enums;

public enum CategoriaMedicamento {
    ONCOLOGICO("Oncológico / Quimioterápico"),
    ANTICORPO_MONOCLONAL("Anticorpo Monoclonal"),
    ONCO_HEMATOLOGICO("Onco-Hematológico"),
    HOSPITALAR_GERAL("Hospitalar Geral / Injetáveis"),
    HOSPITALAR_INJETAVEL("Hospitalar Injetável"),
    SUPORTE_ONCOLOGICO("Terapia de Suporte Oncológico");

    private final String descricao;

    CategoriaMedicamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
