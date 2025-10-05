package com.xavelo.sqs.application.service.exception;

public class DuplicatedSongQuoteException extends RuntimeException {

    public DuplicatedSongQuoteException(String quote) {
        super(String.format("A quote with the same text already exists: %s", quote));
    }

    public DuplicatedSongQuoteException(String quote, Throwable cause) {
        super(String.format("A quote with the same text already exists: %s", quote), cause);
    }

    public DuplicatedSongQuoteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
