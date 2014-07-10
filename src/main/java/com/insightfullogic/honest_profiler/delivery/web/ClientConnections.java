package com.insightfullogic.honest_profiler.delivery.web;

import org.webbitserver.WebSocketConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Collections.synchronizedList;

public class ClientConnections {

    private final List<WebSocketConnection> connections;

    private Consumer<WebSocketConnection> listener;

    public ClientConnections() {
        this.connections = synchronizedList(new ArrayList<>());
    }

    public void add(WebSocketConnection connection) {
        connections.add(connection);
        listener.accept(connection);
    }

    public void remove(WebSocketConnection connection) {
        connections.remove(connection);
    }

    public void sendAll(String message) {
        synchronized (connections) {
            connections.forEach(connection -> connection.send(message));
        }
    }

    public void setListener(Consumer<WebSocketConnection> listener) {
        this.listener = listener;
    }
}
