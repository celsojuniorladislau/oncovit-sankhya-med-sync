package com.oncovit.medsync.domain.enums;

public enum TipoMovimentacao {
    ENTRADA_NOTA("Entrada por Nota Fiscal de Fornecedor / Fabricante"),
    BAIXA_SANKHYA_VENDA("Baixa de Estoque por Venda / Atendimento Sankhya ERP"),
    PERDA_AVARIA("Baixa por Desvio Térmico / Avaria Física"),
    VENCIMENTO("Baixa por Expirar Validade / Descarte Farmacêutico"),
    AJUSTE_INVENTARIO("Ajuste de Auditoria de Inventário Físico"),
    ENTRADA("Entrada Geral"),
    SAIDA_VENDA("Saída por Venda Geral");

    private final String descricao;

    TipoMovimentacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isSaida() {
        return this != ENTRADA_NOTA && this != ENTRADA;
    }
}
