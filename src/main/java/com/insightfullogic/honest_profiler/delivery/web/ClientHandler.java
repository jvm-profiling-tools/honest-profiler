package com.insightfullogic.honest_profiler.delivery.web;

import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebSocketConnection;

public class ClientHandler extends BaseWebSocketHandler {

    private final ClientConnections clients;

    public ClientHandler(ClientConnections clients) {
        this.clients = clients;
    }

    public void onOpen(WebSocketConnection connection) {
        clients.add(connection);
    }

    public void onClose(WebSocketConnection connection) {
        clients.add(connection);
    }

    public void onMessage(WebSocketConnection connection, String message) {

    }

}
