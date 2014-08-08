package com.insightfullogic.honest_profiler.core.filters;

public final class FilterParseException extends RuntimeException {

    public FilterParseException(String message) {
        super(message);
    }

    public FilterParseException(Exception e) {
        super(e);
    }
}
