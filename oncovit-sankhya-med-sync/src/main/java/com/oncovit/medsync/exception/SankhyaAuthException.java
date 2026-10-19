package com.oncovit.medsync.exception;

public class SankhyaAuthException extends BusinessException {

    public SankhyaAuthException(String message) {
        super(message, "UNAUTHORIZED", 401);
    }
}
