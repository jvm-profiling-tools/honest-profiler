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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * .
 */
public class Terminal implements Console
{

    public static final int QUIT = 'q';

    private final InputStream input;
    private final PrintStream output;
    private final Runnable quit;

    private volatile Screen screen;

    public Terminal(InputStream input, PrintStream output, Runnable quit)
    {
        this.input = input;
        this.output = output;
        this.quit = quit;
        this.screen = i -> {
        };
    }

    public void display(Screen screen)
    {
        this.screen.onHide();
        this.screen = screen;
        screen.onShow();
    }

    public boolean isDisplayedScreen(Screen screen)
    {
        return this.screen == screen;
    }

    public void run()
    {
        try
        {
            int inputChar;
            do
            {
                inputChar = input.read();
                if (inputChar == QUIT)
                {
                    quit.run();
                    return;
                }

                screen.handleInput(inputChar);

            } while (true);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public PrintStream stream()
    {
        return output;
    }

}
