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
package com.insightfullogic.honest_profiler.ports.sources;

import com.insightfullogic.honest_profiler.core.Conductor;
import com.insightfullogic.honest_profiler.core.MachineListener;
import com.insightfullogic.honest_profiler.core.Monitor;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import org.slf4j.Logger;
import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebSocketConnection;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketMachineSource extends BaseWebSocketHandler
{

    private final Logger logger;
    private final Map<WebSocketConnection, Conductor> machines;
    private final Monitor monitor;
    private final MachineListener listener;

    public WebSocketMachineSource(final Logger logger, final Monitor monitor, final MachineListener listener)
    {
        this.logger = logger;
        this.monitor = monitor;
        this.listener = listener;
        machines = new ConcurrentHashMap<>();
    }

    @Override
    public void onOpen(WebSocketConnection connection)
    {

    }

    @Override
    public void onClose(WebSocketConnection connection)
    {
        /*DataConsumer consumer = machines.remove(connection);
        if (consumer != null) {
            VirtualMachine machine = consumer.getMachine();
            listener.onClosedMachine(machine);
        }*/
    }

    @Override
    public void onMessage(WebSocketConnection connection, byte[] message)
    {
        if (machines.containsKey(connection))
        {
            ByteBuffer buffer = ByteBuffer.wrap(message);
            /*DataConsumer consumer = machines.get(connection);
            if (consumer != null) {
                consumer.accept(buffer);
            }*/
        }
        else
        {
            newMachine(connection, message);
        }
    }

    private void newMachine(WebSocketConnection connection, byte[] message)
    {
            /*Messages.NewMachine newMachine = Messages.NewMachine.parseFrom(message);
            VirtualMachine machine = new VirtualMachine(newMachine.getId(), newMachine.getDisplayName(), true, "");
            listener.onNewMachine(machine);
            */
            /*ProfileListener profileListener = listener.onNewMachine(machine);
            if (profileListener != null) {
                DataConsumer consumer = conductor.pipeData(machine, profileListener);
                machines.put(connection, consumer);
            }*/
    }

}
