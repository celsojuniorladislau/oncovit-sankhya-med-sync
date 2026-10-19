package com.oncovit.medsync.domain.enums;

public enum StatusLote {
    QUARENTENA("Em Quarentena - Aguardando Liberação do RT / Conferência de CoA e Cadeia Fria"),
    LIBERADO("Liberado para Expedição e Faturamento"),
    BLOQUEADO("Bloqueado por Desvio de Qualidade, Quebra Térmica ou Recall"),
    REJEITADO("Rejeitado no Recebimento / Laudo Inconforme"),
    VENCIDO("Vencido - Descarte Sanitário Obrigatório");

    private final String descricao;

    StatusLote(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean permiteExpedicao() {
        return this == LIBERADO;
    }

    public boolean isVencidoOuInvalido() {
        return this == VENCIDO || this == REJEITADO || this == BLOQUEADO;
    }
}
