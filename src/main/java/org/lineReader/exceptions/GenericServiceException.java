package org.lineReader.exceptions;

public class GenericServiceException extends RuntimeException{

    // Constructor with custom message
    public GenericServiceException(String message) {
        super(message);
    }
}
