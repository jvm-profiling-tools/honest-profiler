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
package com.insightfullogic.honest_profiler.testing_utilities;

import com.insightfullogic.honest_profiler.core.platform.Platforms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.function.Consumer;


public class NonInteractiveAgentRunner
{
    private static final Logger logger = LoggerFactory.getLogger(NonInteractiveAgentRunner.class);

    private final String className;
    private final String args;

    private String errorMessage;
    private boolean isSuccessful;

    private NonInteractiveAgentRunner(final String className, final String args)
    {
        this.className = className;
        this.args = args;
        isSuccessful = true;
    }

    public static void run(final String className, final Consumer<NonInteractiveAgentRunner> handler) throws IOException
    {
        run(className, (String) null, handler);
    }

    public static void run(final String className,
                           final String[] args,
                           final Consumer<NonInteractiveAgentRunner> handler) throws IOException
    {
        run(className, String.join(",", args), handler);
    }

    public static void run(final String className,
                           final String args,
                           final Consumer<NonInteractiveAgentRunner> handler) throws IOException
    {
        NonInteractiveAgentRunner runner = new NonInteractiveAgentRunner(className, args);
        runner.startProcess();
        handler.accept(runner);
    }

    private void startProcess() throws IOException
    {
        String java = System.getProperty("java.home") + "/bin/java";
        String agentArg = "-agentpath:build/liblagent" + Platforms.getDynamicLibraryExtension() + (args != null ? "=" + args : "");
        // Eg: java -agentpath:build/liblagent.so -cp target/classes/ AgentApiExample
        Process process = new ProcessBuilder()
            .command(java, agentArg, "-cp", "target/classes/", className)
            .redirectErrorStream(true)
            .start();

        StringBuilder outputBuffer = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())))
        {
            String line;
            while ((line = reader.readLine()) != null) {
                if (isSuccessful && line.contains("Exception in thread")) 
                    isSuccessful = false;
                outputBuffer.append(line).append('\n');
            }
        }
        errorMessage = outputBuffer.toString();
    }

    public boolean isSuccessful() 
    {
        return isSuccessful;
    }

    public String getErrorMessages() {
        return errorMessage;
    }
}
