package com.au.redenergy.exception;

public class IllegalInputFileException extends IllegalArgumentException {
    public IllegalInputFileException(String errorMessage) {
        super(errorMessage);
    }
}
