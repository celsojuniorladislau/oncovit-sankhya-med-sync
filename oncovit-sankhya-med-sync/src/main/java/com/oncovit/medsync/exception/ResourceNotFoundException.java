package com.oncovit.medsync.exception;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(message, "NOT_FOUND", 404);
    }

    public ResourceNotFoundException(String message, String errorCode, int httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
