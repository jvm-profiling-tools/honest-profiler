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

import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import org.junit.runner.RunWith;

import java.io.InputStream;
import java.io.PrintStream;

import static com.insightfullogic.lambdabehave.Suite.describe;
import static org.mockito.Mockito.*;

@RunWith(JunitSuiteRunner.class)
public class TerminalTest
{
    {

        describe("A Terminal", it -> {

            InputStream in = mock(InputStream.class);
            PrintStream out = mock(PrintStream.class);
            Runnable quit = mock(Runnable.class);
            Screen screen = mock(Screen.class);
            Screen oldScreen = mock(Screen.class);

            Terminal terminal = new Terminal(in, out, quit);

            it.should("quit when q is pressed", expect -> {
                given:
                when(in.read()).thenReturn((int) 'q');

                when:
                terminal.run();

                then:
                verify(quit, times(1)).run();
            });

            it.should("pass on input to its handler", expect -> {
                int one = (int) '1';

                given:
                when(in.read()).thenReturn(one, (int) 'q');
                terminal.display(screen);

                when:
                terminal.run();

                then:
                verify(screen, times(1)).handleInput(one);
            });

            it.should("show new screens", expect -> {
                when:
                terminal.display(screen);

                verify(screen, times(1)).onShow();
            });

            it.should("hide old screens", expect -> {
                given:
                terminal.display(oldScreen);

                when:
                terminal.display(screen);

                verify(oldScreen, times(1)).onHide();
            });

        });

    }}
