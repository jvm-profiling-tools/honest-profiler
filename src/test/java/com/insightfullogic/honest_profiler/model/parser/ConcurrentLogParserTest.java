package com.insightfullogic.honest_profiler.model.parser;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;

import static java.nio.channels.FileChannel.MapMode.READ_WRITE;
import static org.junit.Assert.assertTrue;

public class ConcurrentLogParserTest {

    private static final int SIZE = 1024 * 1024 * 100;

    @Test
    public void canParseLogBeingConcurrentlyWrittenTo() throws IOException {
        File tmpFile = File.createTempFile("test-log", "hpl");

        try (FileInputStream input = new FileInputStream(Logs.log0());
             RandomAccessFile log = new RandomAccessFile(tmpFile, "rw")) {

            MappedByteBuffer buffer = log.getChannel().map(READ_WRITE, 0, SIZE);

            FakeEventListener events = new FakeEventListener();
            LogParser parser = new LogParser(events);

            parser.monitor(tmpFile);

            copyAndCheckFirstEvent(input, buffer, events);
            copyAndCheckSecondEvent(input, buffer, events);
        } finally {
            tmpFile.delete();
        }
    }

    private void copyAndCheckSecondEvent(FileInputStream input, MappedByteBuffer buffer, FakeEventListener events) throws IOException {
        copy(input, buffer, 13);
        sleep(50L);
        events.seenEventCount(2);
        events.hasSeenEvent(new StackFrame(52, 1));
    }

    private void copyAndCheckFirstEvent(FileInputStream input, MappedByteBuffer buffer, FakeEventListener events) throws IOException {
        copy(input, buffer, 13);
        sleep(50L);
        events.seenEventCount(1);
        events.hasSeenEvent(new TraceStart(2, 5));
    }

    private void copy(FileInputStream input, MappedByteBuffer output, int sizeInBytes) throws IOException {
        byte[] buffer = new byte[sizeInBytes];
        input.read(buffer);
        output.put(buffer);
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
