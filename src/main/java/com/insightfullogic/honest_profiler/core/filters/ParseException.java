package com.insightfullogic.honest_profiler.core.filters;

public final class ParseException extends RuntimeException {

    public ParseException(String message) {
        super(message);
    }

    public ParseException(Exception e) {
        super(e);
    }
}
