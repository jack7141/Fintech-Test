package com.moin.remittance.exception;

public class UnAuthorizationJwtException extends RuntimeException {
    public UnAuthorizationJwtException(String message) {
        super(message);
    }
}
