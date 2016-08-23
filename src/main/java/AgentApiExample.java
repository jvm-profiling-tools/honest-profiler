import com.insightfullogic.honest_profiler.core.control.Agent;

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
public class AgentApiExample
{

    public static void main(String[] args) throws Exception
    {
        final String fileNameInit = "/tmp/log-1.hpl";
        final String fileNameUpd = "/tmp/log-2.hpl";
        final int samplingIntervalInit = 41;
        final int samplingIntervalUpd = 333;
        final int maxFramesToCaptureInit = 42;
        final int maxFramesToCaptureUpd = 121;

        Agent.setFilePath(fileNameInit);
        assertEquals(fileNameInit, Agent.getFilePath(), "set initial logs file path");
            
        Agent.setSamplingInterval(samplingIntervalInit, 2 * samplingIntervalInit);
        assertEquals(samplingIntervalInit, Agent.getSamplingIntervalMin(), "set initial min sampling interval");
        assertEquals(2 * samplingIntervalInit, Agent.getSamplingIntervalMax(), "set initial max sampling interval");
            
        Agent.setMaxFramesToCapture(maxFramesToCaptureInit);
        assertEquals(maxFramesToCaptureInit, Agent.getMaxFramesToCapture(), "set initial max stack frames to capture");

        Agent.start();

        Agent.setFilePath(fileNameUpd);
        assertEquals(fileNameInit, Agent.getFilePath(), "update logs file path when profiler is running");
            
        Agent.setSamplingInterval(samplingIntervalUpd, 2 * samplingIntervalUpd);
        assertEquals(samplingIntervalInit, Agent.getSamplingIntervalMin(), "update min sampling interval when profiler is running");
        assertEquals(2 * samplingIntervalInit, Agent.getSamplingIntervalMax(), "update max sampling interval when profiler is running");
            
        Agent.setMaxFramesToCapture(maxFramesToCaptureUpd);
        assertEquals(maxFramesToCaptureInit, Agent.getMaxFramesToCapture(), "update max stack frames to capture when profiler is running");

        Agent.stop();

        Agent.setFilePath(fileNameUpd);
        assertEquals(fileNameUpd, Agent.getFilePath(), "update logs file path");
            
        Agent.setSamplingInterval(samplingIntervalUpd, 2 * samplingIntervalUpd);
        assertEquals(samplingIntervalUpd, Agent.getSamplingIntervalMin(), "update min sampling interval");
        assertEquals(2 * samplingIntervalUpd, Agent.getSamplingIntervalMax(), "update max sampling interval");
            
        Agent.setMaxFramesToCapture(maxFramesToCaptureUpd);
        assertEquals(maxFramesToCaptureUpd, Agent.getMaxFramesToCapture(), "update max stack frames to capture");
    }

    private static void assertEquals(Object expected, Object actual, String description) 
    {
        if (!expected.equals(actual)) 
        {
            throw new RuntimeException(
                String.format("Assertion failed for '%s'. Expected '%s', actual '%s'", 
                    description, expected.toString(), actual.toString())
            );
        }
    }
}
