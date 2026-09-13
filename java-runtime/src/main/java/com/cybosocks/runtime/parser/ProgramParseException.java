package com.cybosocks.runtime.parser;

/**
 * Thrown when program JSON cannot be parsed or fails validation.
 * Message is already user-facing (starts with "Error: ...").
 */
public class ProgramParseException extends RuntimeException {

    public ProgramParseException(String message) {
        super(message);
    }
}
