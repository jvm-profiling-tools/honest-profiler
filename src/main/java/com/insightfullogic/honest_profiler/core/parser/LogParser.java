/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
package com.insightfullogic.honest_profiler.core.parser;

import org.slf4j.Logger;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import static com.insightfullogic.honest_profiler.core.parser.LogParser.AmountRead.*;

public class LogParser
{

    private static final int NOT_WRITTEN = 0;
    private static final int TRACE_START = 1;
    private static final int STACK_FRAME_BCI_ONLY = 2;
    private static final int STACK_FRAME_FULL = 21;
    private static final int NEW_METHOD = 3;

    private final LogEventListener listener;
    private final Logger logger;

    public static enum AmountRead
    {
        COMPLETE_RECORD, PARTIAL_RECORD, NOTHING
    }

    public LogParser(final Logger logger, final LogEventListener listener)
    {
        this.listener = listener;
        this.logger = logger;
    }

    public AmountRead readRecord(ByteBuffer input)
    {
        int initialPosition = input.position();

        if (!input.hasRemaining())
        {
            return NOTHING;
        }

        byte type = input.get();
        try
        {
            switch (type)
            {
                case NOT_WRITTEN:
                    // go back one byte since we've just read a 0
                    input.position(input.position() - 1);
                    return NOTHING;
                case TRACE_START:
                    readTraceStart(input);
                    return COMPLETE_RECORD;
                case STACK_FRAME_BCI_ONLY:
                    readStackFrameBciOnly(input);
                    return COMPLETE_RECORD;
                case STACK_FRAME_FULL:
                    readStackFrameFull(input);
                    return COMPLETE_RECORD;
                case NEW_METHOD:
                    readNewMethod(input);
                    return COMPLETE_RECORD;
            }
        }
        catch (BufferUnderflowException e)
        {
            // If you've underflowed the buffer,
            // then you need to wait for more data to be written.
            input.position(initialPosition);
            return PARTIAL_RECORD;
        }

        // Should never get here
        return NOTHING;
    }

    public void endOfLog()
    {
        listener.endOfLog();
    }

    private void readNewMethod(ByteBuffer input)
    {
        Method newMethod = new Method(input.getLong(), readString(input), readString(input), readString(input));
        newMethod.accept(listener);
    }

    private String readString(ByteBuffer input)
    {
        int size = input.getInt();
        char[] buffer = new char[size];
        // conversion from c style characters to Java.
        for (int i = 0; i < size; i++)
        {
            buffer[i] = (char) input.get();
        }
        return new String(buffer);
    }

    private void readStackFrameBciOnly(ByteBuffer input)
    {
        int bci = input.getInt();
        long methodId = input.getLong();
        StackFrame stackFrame = new StackFrame(bci, methodId);
        stackFrame.accept(listener);
    }

    private void readStackFrameFull(ByteBuffer input)
    {
        int bci = input.getInt();
        int lineNumber = input.getInt();
        long methodId = input.getLong();
        StackFrame stackFrame = new StackFrame(bci, lineNumber, methodId);
        stackFrame.accept(listener);
    }

    private void readTraceStart(ByteBuffer input)
    {
        int numberOfFrames = input.getInt();
        long threadId = input.getLong();
        TraceStart traceStart = new TraceStart(numberOfFrames, threadId);
        traceStart.accept(listener);
    }

}
