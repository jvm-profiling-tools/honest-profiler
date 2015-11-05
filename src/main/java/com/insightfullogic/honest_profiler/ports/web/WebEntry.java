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

import app.Root;
import com.insightfullogic.honest_profiler.core.Monitor;
import com.insightfullogic.honest_profiler.core.collector.LogCollector;
import com.insightfullogic.honest_profiler.core.parser.LogParser;
import com.insightfullogic.honest_profiler.ports.sources.LocalMachineSource;
import com.insightfullogic.honest_profiler.ports.sources.WebSocketMachineSource;
import com.insightfullogic.honest_profiler.ports.web.store.FileLogRepository;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;
import org.webbitserver.handler.StaticFileHandler;

import java.io.File;
import java.net.URL;

public class WebEntry
{

    private static final int PORT = Integer.parseInt(System.getProperty("port", "8080"));

    public static void main(String[] args) throws Exception
    {
        URL url = Root.class.getResource("index.html");
        String staticDir = new File(url.toURI()).getParent();

        MutablePicoContainer container = registerComponents();
        container.start();

        try
        {
            WebServer server = WebServers.createWebServer(PORT)
                .add("/agents", container.getComponent(WebSocketMachineSource.class))
                .add("/clients", container.getComponent(ClientHandler.class))
                .add(new StaticFileHandler(staticDir));

            server.start().get();
        }
        finally
        {
            container.stop();
        }
    }

    public static MutablePicoContainer registerComponents()
    {
        MutablePicoContainer pico = new PicoBuilder()
            .withJavaEE5Lifecycle()
            .withCaching()
            .build()

            .addComponent(Monitor.class)
            .addComponent(FileLogRepository.class)
            .addComponent(WebSocketMachineSource.class)
            .addComponent(LocalMachineSource.class)
            .addComponent(MessageEncoder.class)
            .addComponent(WebsocketMachineAdapter.class)
            .addComponent(WebProfileAdapter.class)
            .addComponent(ClientConnections.class)
            .addComponent(ClientHandler.class)
            .addComponent(LogCollector.class)
            .addComponent(LogParser.class);

        return pico.addComponent(pico);
    }

}
