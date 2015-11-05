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

import org.webbitserver.WebSocketConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Collections.synchronizedList;

public class ClientConnections
{

    private final List<WebSocketConnection> connections;

    private Consumer<WebSocketConnection> listener;

    public ClientConnections()
    {
        this.connections = synchronizedList(new ArrayList<>());
    }

    public void add(WebSocketConnection connection)
    {
        connections.add(connection);
        listener.accept(connection);
    }

    public void remove(WebSocketConnection connection)
    {
        connections.remove(connection);
    }

    public void sendAll(String message)
    {
        synchronized (connections)
        {
            connections.forEach(connection -> connection.send(message));
        }
    }

    public void setListener(Consumer<WebSocketConnection> listener)
    {
        this.listener = listener;
    }
}
