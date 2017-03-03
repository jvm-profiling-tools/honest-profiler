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
package com.insightfullogic.honest_profiler.ports;

import com.insightfullogic.honest_profiler.core.MachineListener;
import com.insightfullogic.honest_profiler.core.Monitor;
import com.insightfullogic.honest_profiler.core.profiles.ProfileListener;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.honest_profiler.ports.sources.WebSocketMachineSource;
import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.webbitserver.WebSocketConnection;

import static com.insightfullogic.lambdabehave.Suite.describe;
import static org.mockito.Mockito.*;

@Ignore
@RunWith(JunitSuiteRunner.class)
public class WebSocketMachineSourceTest
{

    WebSocketMachineSource finder;

    {
        describe("Web Socket Machine Finder", it -> {

            byte[] newMachine = null;

            VirtualMachine machine = new VirtualMachine("123@erdos", "com.intellij.idea.Main", true, "", "");
            WebSocketConnection connection = mock(WebSocketConnection.class);
            //DataConsumer dataConsumer = mock(DataConsumer.class);
            Monitor monitor = mock(Monitor.class);
            MachineListener listener = mock(MachineListener.class);
            ProfileListener profileListener = mock(ProfileListener.class);
            Logger logger = mock(Logger.class);

            it.isSetupWith(() -> {
            /*reset(connection, dataConsumer, monitor, listener);
            when(dataConsumer.getMachine()).thenReturn(machine);*/

                finder = new WebSocketMachineSource(logger, monitor, listener);
            });

            it.should("initially know of no machines", expect -> {
                verifyNoMoreInteractions(listener);
            });

            it.should("recognise a new machine", expect -> {
                when:
                finder.onMessage(connection, newMachine);

                then:
                verify(listener).onNewMachine(machine);
                verifyNoMoreInteractions(listener);
            });

            it.should("recognise a removed machine", expect -> {
                given:
                finder.onMessage(connection, newMachine);
                verify(listener).onNewMachine(machine);

                when:
                finder.onClose(connection);

                then:
                verify(listener).onClosedMachine(machine);
                verifyNoMoreInteractions(listener);
            });

            it.should("cope with connections being opened and closed erratically", expect -> {
                given:
                finder.onOpen(connection);

                when:
                finder.onClose(connection);

                then:
                verifyNoMoreInteractions(listener);
            });

            it.should("cope with invalid data", expect -> {
                when:
                finder.onMessage(connection, new byte[]{});

                then:
                verifyNoMoreInteractions(listener);
            });

            it.should("send data to the log consumer", expect -> {
                given:
                finder.onMessage(connection, newMachine);
                byte[] data = {0, 1, 2, 3};

                when:
                finder.onMessage(connection, data);

            /*then:
            verify(dataConsumer).accept(ByteBuffer.wrap(data));*/
            });

        });
    }
}
