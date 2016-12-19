package com.insightfullogic.honest_profiler.core.sources;

public class CantReadFromSourceException extends RuntimeException
{
    public CantReadFromSourceException(final Throwable cause)
    {
        super(cause);
    }
}
