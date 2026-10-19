package com.oncovit.medsync.exception;

import java.time.LocalDate;

public class ExpiredLotException extends BusinessException {

    private final String numeroLote;
    private final LocalDate dataValidade;

    public ExpiredLotException(String message, String numeroLote, LocalDate dataValidade) {
        super(message, "EXPIRED_LOT_REJECTED", 422);
        this.numeroLote = numeroLote;
        this.dataValidade = dataValidade;
    }

    public String getNumeroLote() {
        return numeroLote;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }
}
