package com.insightfullogic.honest_profiler.delivery.web;

import app.Root;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;
import org.webbitserver.handler.StaticFileHandler;

import java.io.File;

public class WebEntry {

    private static final int PORT = Integer.parseInt(System.getProperty("port", "8080"));

    public static void main(String[] args) throws Exception {

        String dir = new File(Root.class.getResource("index.html").toURI()).getParent();

        WebServer webServer = WebServers.createWebServer(PORT)
                .add("/websocket", new ConnectionHandler())
                .add(new StaticFileHandler(dir))
                .start()
                .get();
    }

}
