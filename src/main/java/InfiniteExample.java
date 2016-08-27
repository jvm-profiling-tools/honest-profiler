import com.insightfullogic.honest_profiler.core.control.Agent;

import java.lang.management.ManagementFactory;

import static java.lang.Long.parseLong;

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
public class InfiniteExample
{

    public static void main(String[] args) throws Exception
    {
        final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        final int index = jvmName.indexOf('@');
        final Thread control = new Thread(InfiniteExample::startOrStop);

        System.out.println(parseLong(jvmName.substring(0, index)));
        control.start();

        while (true)
        {
            Thread.sleep(1);
            subMethod();
        }
    }

    private static void subMethod()
    {
        System.out.println("calling some code, lalala");
    }

    private static void startOrStop()
    {
        try
        {
            while (true)
            {
                int ch = System.in.read();

                if (ch == 'S')
                    Agent.start();
                else if (ch == 's')
                    Agent.stop();
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
