package com.insightfullogic.honest_profiler.log;

import java.io.*;
import java.nio.*;

public class LogParser {

    private static final int TRACE_START = 0;
    private static final int STACK_FRAME = 1;
    private static final int NEW_METHOD = 2;

    private final EventListener listener;

    public LogParser(EventListener listener) {
        this.listener = listener;
    }

    public void parse(File file) {
        try {
            DataInputStream input = new DataInputStream(new FileInputStream(file));
            while (readRecord(input))
                ;
            listener.endOfLog();
        } catch (IOException e) {
            throw new ParseException(e);
        }
    }

    private boolean readRecord(DataInputStream input) throws IOException {
        int type = input.read();
        if (type == -1) {
            return false;
        }

        try {
            switch (type) {
                case TRACE_START:
                    readTraceStart(input);
                    return true;
                case STACK_FRAME:
                    readStackFrame(input);
                    return true;
                case NEW_METHOD:
                    readNewMethod(input);
                    return true;
            }
            return false;
        } catch (EOFException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void readNewMethod(DataInputStream input) throws IOException {
        Method newMethod = new Method(input.readLong(), readString(input), readString(input), readString(input));
        newMethod.accept(listener);
    }

    private String readString(DataInputStream input) throws IOException {
        int size = input.readInt();
        char[] buffer = new char[size];
        // conversion from c style characters to Java.
        for(int i = 0; i < size; i++) {
            buffer[i] = (char) input.readByte();
        }
        return new String(buffer);
    }

    private void readStackFrame(DataInputStream input) throws IOException {
        int lineNumber = input.readInt();
        long methodId = input.readLong();
        StackFrame stackFrame = new StackFrame(lineNumber, methodId);
        stackFrame.accept(listener);
    }

    private void readTraceStart(DataInputStream input) throws IOException {
        int numberOfFrames = input.readInt();
        long threadId = input.readLong();
        TraceStart traceStart = new TraceStart(numberOfFrames, threadId);
        traceStart.accept(listener);
    }

}
