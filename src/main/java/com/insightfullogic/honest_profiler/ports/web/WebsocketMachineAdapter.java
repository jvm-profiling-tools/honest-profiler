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
package com.insightfullogic.honest_profiler.ports.web;

import com.insightfullogic.honest_profiler.core.MachineListener;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import org.webbitserver.WebSocketConnection;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

// Not thread safe
public class WebsocketMachineAdapter implements MachineListener, Consumer<WebSocketConnection>
{

    private final Set<VirtualMachine> machines;
    private final ClientConnections clients;
    private final MessageEncoder messages;

    public WebsocketMachineAdapter(ClientConnections clients, MessageEncoder messages)
    {
        this.clients = clients;
        this.messages = messages;
        machines = new HashSet<>();
        clients.setListener(this);
    }

    @Override
    public void accept(WebSocketConnection connection)
    {
        machines.forEach(machine -> {
            connection.send(messages.addJavaVirtualMachine(machine));
        });
    }

    @Override
    public void onNewMachine(VirtualMachine machine)
    {
        machines.add(machine);
        clients.sendAll(messages.addJavaVirtualMachine(machine));
        //return new WebProfileAdapter(LoggerFactory.getLogger(WebProfileAdapter.class), machine, clients);
    }

    @Override
    public void onClosedMachine(VirtualMachine machine)
    {
        machines.remove(machine);
        clients.sendAll(messages.removeJavaVirtualMachine(machine));
    }

}
