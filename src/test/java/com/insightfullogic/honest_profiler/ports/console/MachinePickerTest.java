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


import com.insightfullogic.honest_profiler.ports.sources.LocalMachineSource;
import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import org.junit.runner.RunWith;

import static com.insightfullogic.honest_profiler.testing_utilities.VirtualMachineFixtures.vmNoAgent;
import static com.insightfullogic.honest_profiler.testing_utilities.VirtualMachineFixtures.vmWithAgent;
import static com.insightfullogic.lambdabehave.Suite.describe;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(JunitSuiteRunner.class)
public class MachinePickerTest
{
    {

        describe("The Machine Picker Screen", it -> {

            Terminal terminal = mock(Terminal.class);
            MachinePickerView view = mock(MachinePickerView.class);
            LocalMachineSource source = mock(LocalMachineSource.class);
            MachinePicker picker = new MachinePicker(view, terminal, ignore -> source);

            it.isSetupWith(() -> {
                reset(terminal, view, source);
                picker.onShow();
                when(terminal.isDisplayedScreen(picker)).thenReturn(true);
            });

            it.should("start searching for machines when its displayed", expect -> {
                then:
                verify(source).start();
            });

            it.should("display a list of jvms", expect -> {
                when:
                picker.onNewMachine(vmNoAgent);
                picker.onNewMachine(vmWithAgent);

                then:
                view.render(vmNoAgent, 0);
                view.render(vmWithAgent, 1);
            });

            it.should("ignore irrelevant input", expect -> {
                given:
                verify(source).start();

                when:
                picker.handleInput('d');

                then:
                verifyNoMoreInteractions(source, terminal);
            });

            it.should("switches display when vm is picked", expect -> {
                given:
                picker.onNewMachine(vmNoAgent);
                picker.onNewMachine(vmWithAgent);

                when:
                picker.handleInput((int) '1');

                then:
                verify(terminal).display(any(ProfileScreen.class));
            });

            it.should("stop searching for machines when its displayed", expect -> {
            /*when:
            picker.handleInput((int) '0');

            then:
            verify(source).start();*/
            });


        });

    }}
