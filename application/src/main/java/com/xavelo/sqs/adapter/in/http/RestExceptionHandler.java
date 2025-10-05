package com.xavelo.sqs.adapter.in.http;

import com.xavelo.sqs.application.service.exception.DuplicatedSongQuoteException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

    private static final Logger logger = LogManager.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(DuplicatedSongQuoteException.class)
    public ResponseEntity<Void> handleDuplicatedSongQuoteException(DuplicatedSongQuoteException exception) {
        logger.warn("Duplicated quote detected: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}
