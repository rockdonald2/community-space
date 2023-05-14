package edu.pdae.cs.hubmgmt.controller.exception;

public class ConflictingOperationException extends RuntimeException {

    public ConflictingOperationException(String message) {
        super(message);
    }

    public ConflictingOperationException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
