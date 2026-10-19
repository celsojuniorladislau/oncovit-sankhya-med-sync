package com.oncovit.medsync.domain.enums;

public enum StatusTransacaoSankhya {
    SUCCESS(200, "Baixa de estoque efetuada com sucesso"),
    OUT_OF_STOCK(409, "Saldo insuficiente no lote"),
    EXPIRED_LOT_REJECTED(422, "Lote vencido - rejeitado por barreira sanitária"),
    LOT_NOT_RELEASED(422, "Lote não liberado para faturamento"),
    LOT_NOT_FOUND(404, "Lote não encontrado no catálogo"),
    PRODUCT_NOT_FOUND(404, "Produto não encontrado"),
    INVALID_PAYLOAD(400, "Dados de requisição inválidos"),
    UNAUTHORIZED(401, "Credenciais Sankhya inválidas ou ausentes");

    private final int httpStatus;
    private final String mensagemPadrao;

    StatusTransacaoSankhya(int httpStatus, String mensagemPadrao) {
        this.httpStatus = httpStatus;
        this.mensagemPadrao = mensagemPadrao;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getMensagemPadrao() {
        return mensagemPadrao;
    }
}
