package com.insightfullogic.honest_profiler.delivery.web;

import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebSocketConnection;

public class ClientHandler extends BaseWebSocketHandler {

    private final Connections connections;

    public ClientHandler(Connections connections) {
        this.connections = connections;
    }

    public void onOpen(WebSocketConnection connection) {
        connections.add(connection);
    }

    public void onClose(WebSocketConnection connection) {
        connections.add(connection);
    }

    public void onMessage(WebSocketConnection connection, String message) {

    }

}
