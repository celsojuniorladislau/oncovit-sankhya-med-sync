package com.oncovit.medsync.exception;

public class InsufficientStockException extends BusinessException {

    private final String numeroLote;
    private final int quantidadeSolicitada;
    private final int quantidadeDisponivel;

    public InsufficientStockException(String message, String numeroLote, int quantidadeSolicitada, int quantidadeDisponivel) {
        super(message, "OUT_OF_STOCK", 409);
        this.numeroLote = numeroLote;
        this.quantidadeSolicitada = quantidadeSolicitada;
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public String getNumeroLote() {
        return numeroLote;
    }

    public int getQuantidadeSolicitada() {
        return quantidadeSolicitada;
    }

    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }
}
