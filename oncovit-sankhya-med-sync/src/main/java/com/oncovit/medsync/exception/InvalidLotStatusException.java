package com.oncovit.medsync.exception;

public class InvalidLotStatusException extends BusinessException {

    private final String numeroLote;
    private final String statusAtual;

    public InvalidLotStatusException(String message, String numeroLote, String statusAtual) {
        super(message, "LOT_NOT_RELEASED", 422);
        this.numeroLote = numeroLote;
        this.statusAtual = statusAtual;
    }

    public String getNumeroLote() {
        return numeroLote;
    }

    public String getStatusAtual() {
        return statusAtual;
    }
}
