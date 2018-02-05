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
// These names match the names reported by the forte quality kit(AKA AsyncGetCallTrace)
//  enum {
//    ticks_no_Java_frame         =  0,
//    ticks_no_class_load         = -1,
//    ticks_GC_active             = -2,
//    ticks_unknown_not_Java      = -3,
//    ticks_not_walkable_not_Java = -4,
//    ticks_unknown_Java          = -5,
//    ticks_not_walkable_Java     = -6,
//    ticks_unknown_state         = -7,
//    ticks_thread_exit           = -8,
//    ticks_deopt                 = -9,
//    ticks_safepoint             = -10
//  };

    private static final String[] AGCT_ERRORS = {"NoJavaFramesErr0",
            "NoClassLoadErr1",
            "GcActiveErr2",
            "UnknownNotJava3",
            "NotWalkableNotJavaErr4",
            "UnknownJavaErr5",
            "NotWalkableJavaErr6",
            "UnknownStateErr7",
            "ThreadExitErr8",
            "DeoptErr9",
            "SafepointErr10"};
    private static final int NOT_WRITTEN = 0;
    private static final int TRACE_START = 1;
    private static final int TRACE_WITH_TIME = 11;
    private static final int STACK_FRAME_BCI_ONLY = 2;
    private static final int STACK_FRAME_FULL = 21;
    private static final int NEW_METHOD = 3;
    private static final int NEW_METHOD_SIGNATURE = 31;
    private static final int THREAD_META = 4;

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
        // report the different errors as methods
        for (long errId = 0; errId < AGCT_ERRORS.length; errId++)
        {
            // we use negative jmethodIds, these are invalid addresses and should not collide with jmethodIds supplied by JVM
            new Method(-errId - 1, "", "-AGCT-", AGCT_ERRORS[(int) errId]).accept(listener);
        }
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
                    readTraceStart(input, false);
                    return COMPLETE_RECORD;
                case TRACE_WITH_TIME:
                    readTraceStart(input, true);
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
                case NEW_METHOD_SIGNATURE:
                    readNewMethodSignature(input);
                    return COMPLETE_RECORD;
                case THREAD_META:
                    readNewThreadMeta(input);
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

    private void readNewMethodSignature(ByteBuffer input)
    {
        Method newMethod = new Method(input.getLong(), readString(input), readString(input), readString(input), 
            readString(input), readString(input), readString(input));
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

    private void readTraceStart(ByteBuffer input, boolean withTime)
    {
        int numberOfFrames = input.getInt();
        long threadId = input.getLong();
        long timeSec = 0L;
        long timeNano = 0L;

        if (withTime) {
            timeSec = input.getLong();
            timeNano = input.getLong();
        }

        // number of frames <= 0 -> error, so log a mock stack frame reflecting the error. Logging errors as frames makes
        // more sense when collecting profiles.
        if (numberOfFrames <= 0)
        {
            // if this is an unknown error add a new method for it
            if (-numberOfFrames >= AGCT_ERRORS.length)
            {
                new Method(numberOfFrames - 1, "", "AGCT", "UnknownErrCode"+(-numberOfFrames)).accept(listener);
            }

            // we choose to report errors via frames, so pretend there's a single frame in the trace
            new TraceStart(1, threadId, timeSec, timeNano).accept(listener);
            // we shift the err code by -1 to avoid using the valid NULL jmethodId
            new StackFrame(-1, numberOfFrames - 1).accept(listener);
        }
        else
        {
            TraceStart traceStart = new TraceStart(numberOfFrames, threadId, timeSec, timeNano);
            traceStart.accept(listener);
        }
    }

    private void readNewThreadMeta(ByteBuffer input) {
        long threadId = input.getLong();
        String threadName = readString(input);

        ThreadMeta threadMeta = new ThreadMeta(threadId, threadName);
        threadMeta.accept(listener);
    }
}
