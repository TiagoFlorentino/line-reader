package org.lineReader.exceptions;

public class OutOfBoundsIndexException extends RuntimeException {

    // Constructor with custom message
    public OutOfBoundsIndexException(String message) {
        super(message);
    }
}
