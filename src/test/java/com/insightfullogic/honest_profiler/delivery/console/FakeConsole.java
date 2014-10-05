package com.insightfullogic.honest_profiler.delivery.console;

import com.insightfullogic.honest_profiler.core.parser.Method;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class FakeConsole implements Console {

    private ByteArrayOutputStream buffer;
    private PrintStream out;
    private String output;

    public FakeConsole() {
        clear();
    }

    @Override
    public PrintStream stream() {
        return out;
    }

    public void isShowingTraces(int numberOfStackTraces) {
        outputContains("Number of stack traces: " + numberOfStackTraces);
    }

    public void outputContains(String message) {
        assertThat(getOutput(), containsString(message));
    }

    public void outputDoesntContain(String message) {
        assertThat(getOutput(), not(containsString(message)));
    }

    public String getOutput() {
        if (output == null) {
            output = new String(buffer.toByteArray());
            System.out.println("Received output:");
            System.out.println(output);
        }
        return output;
    }

    public void displaysMethod(Method method) {
        outputContains(method.getClassName());
        outputContains(method.getMethodName());
    }

    public void clear() {
        buffer = new ByteArrayOutputStream();
        out = new PrintStream(buffer);
        output = null;
    }
}
