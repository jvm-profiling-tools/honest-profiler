package com.insightfullogic.honest_profiler.delivery.web;

import app.Root;
import com.insightfullogic.honest_profiler.adapters.source.LocalMachineFinder;
import com.insightfullogic.honest_profiler.adapters.source.WebSocketMachineFinder;
import com.insightfullogic.honest_profiler.model.collector.LogCollector;
import com.insightfullogic.honest_profiler.model.machines.MachineFindingAgent;
import com.insightfullogic.honest_profiler.model.parser.LogParser;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;
import org.webbitserver.WebServers;
import org.webbitserver.handler.StaticFileHandler;

import java.io.File;
import java.net.URL;

public class WebEntry {

    private static final int PORT = Integer.parseInt(System.getProperty("port", "8080"));

    public static void main(String[] args) throws Exception {
        URL url = Root.class.getResource("index.html");
        String staticDir = new File(url.toURI()).getParent();

        MutablePicoContainer container = registerComponents();
        container.start();

        try {
            WebServers.createWebServer(PORT)
                      .add("/agents", container.getComponent(WebSocketMachineFinder.class))
                      .add("/clients", container.getComponent(ClientHandler.class))
                      .add(new StaticFileHandler(staticDir))
                      .start()
                      .get();
        } finally {
            container.stop();
        }
    }

    public static MutablePicoContainer registerComponents() {
        MutablePicoContainer pico = new PicoBuilder()
                .withJavaEE5Lifecycle()
                .withCaching()
                .build()

                .addComponent(WebSocketMachineFinder.class)
                .addComponent(LocalMachineFinder.class)
                .addComponent(MessageEncoder.class)
                .addComponent(MachineAdapter.class)
                .addComponent(ClientConnections.class)
                .addComponent(ClientHandler.class)
                .addComponent(LogCollector.class)
                .addComponent(LogParser.class)
                .addComponent(MachineFindingAgent.class);

        return pico.addComponent(pico);
    }

}
