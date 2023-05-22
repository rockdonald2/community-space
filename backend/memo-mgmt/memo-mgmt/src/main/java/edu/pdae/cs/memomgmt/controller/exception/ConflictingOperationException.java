package edu.pdae.cs.memomgmt.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictingOperationException extends RuntimeException {

    public ConflictingOperationException(String message) {
        super(message);
    }

    public ConflictingOperationException(String message, Throwable cause) {
        super(message, cause);
    }

}
