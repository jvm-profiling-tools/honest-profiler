package com.insightfullogic.honest_profiler.log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.concurrent.ForkJoinPool;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;

public class LogParser {

    private static final int NOT_WRITTEN = 0;
    private static final int TRACE_START = 1;
    private static final int STACK_FRAME = 2;
    private static final int NEW_METHOD = 3;

    public static final int POLL_INTERVAL = 10;

    private final EventListener listener;

    private volatile boolean running;

    public LogParser(EventListener listener) {
        this.listener = listener;
    }

    public void parse(File file) {
        parse(file, false);
    }

    public void monitor(File file) {
        ForkJoinPool.commonPool()
                    .execute(() -> parse(file, true));
    }

    private void parse(File file, boolean continuous) {
        running = true;
        try (RandomAccessFile input = new RandomAccessFile(file, "r")) {
            // Using memory mapped files allows us to use the log as a
            // form of IPC
            MappedByteBuffer buffer = input.getChannel()
                                           .map(READ_ONLY, 0, file.length());

            listener.startOfLog(continuous);
            while (readRecord(buffer, continuous))
                ;
            listener.endOfLog();
        } catch (IOException e) {
            throw new ParseException(e);
        }
    }

    public void stop() {
        running = false;
    }

    private boolean readRecord(ByteBuffer input, boolean continuous) throws IOException {
        if (!input.hasRemaining() || !running)
            return false;

        byte type = input.get();
        try {
            switch (type) {
                case NOT_WRITTEN:
                    if (continuous) {
                        retry(input);
                        return true;
                    } else {
                        return false;
                    }
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
        } catch (BufferUnderflowException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void retry(ByteBuffer input) {
        // back back one byte since we've just read a 0
        //System.out.println("retry");
        input.position(input.position() - 1);
        pause();
    }

    private void pause() {
        try {
            Thread.sleep(POLL_INTERVAL);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void readNewMethod(ByteBuffer input) throws IOException {
        Method newMethod = new Method(input.getLong(), readString(input), readString(input), readString(input));
        newMethod.accept(listener);
    }

    private String readString(ByteBuffer input) throws IOException {
        int size = input.getInt();
        char[] buffer = new char[size];
        // conversion from c style characters to Java.
        for(int i = 0; i < size; i++) {
            buffer[i] = (char) input.get();
        }
        return new String(buffer);
    }

    private void readStackFrame(ByteBuffer input) throws IOException {
        int lineNumber = input.getInt();
        long methodId = input.getLong();
        StackFrame stackFrame = new StackFrame(lineNumber, methodId);
        stackFrame.accept(listener);
    }

    private void readTraceStart(ByteBuffer input) throws IOException {
        int numberOfFrames = input.getInt();
        long threadId = input.getLong();
        TraceStart traceStart = new TraceStart(numberOfFrames, threadId);
        traceStart.accept(listener);
    }

}
