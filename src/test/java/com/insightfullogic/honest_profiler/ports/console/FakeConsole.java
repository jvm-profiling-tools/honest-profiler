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
package com.insightfullogic.honest_profiler.ports.console;

import com.insightfullogic.honest_profiler.core.parser.Method;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class FakeConsole implements Console
{

    private ByteArrayOutputStream buffer;
    private PrintStream out;
    private String output;

    public FakeConsole()
    {
        eraseScreen();
    }

    @Override
    public PrintStream stream()
    {
        return out;
    }

    public void isShowingTraces(int numberOfStackTraces)
    {
        outputContains("Number of stack traces: " + numberOfStackTraces);
    }

    public void outputContains(String message)
    {
        assertThat(getOutput(), containsString(message));
    }

    public void outputDoesntContain(String message)
    {
        assertThat(getOutput(), not(containsString(message)));
    }

    public String getOutput()
    {
        if (output == null)
        {
            output = new String(buffer.toByteArray());
            System.out.println("Received output:");
            System.out.println(output);
        }
        return output;
    }

    public void displaysMethod(Method method)
    {
        outputContains(method.getClassName());
        outputContains(method.getMethodName());
    }

    public void eraseScreen()
    {
        buffer = new ByteArrayOutputStream();
        out = new PrintStream(buffer);
        output = null;
    }
}
