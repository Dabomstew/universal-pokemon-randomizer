package com.dabomstew.pkrandom.exceptions;

public class RandomizerIOException extends RuntimeException {
    public RandomizerIOException(Exception e) {
        super(e);
    }

    public RandomizerIOException(String text) {
        super(text);
    }

    private static final long serialVersionUID = -8174099615381353972L;
}
