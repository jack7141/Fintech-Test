package com.moin.remittance.exception;

public class DuplicateUserIdException extends RuntimeException {
    public DuplicateUserIdException(String message) {
        super(message);
    }
}
