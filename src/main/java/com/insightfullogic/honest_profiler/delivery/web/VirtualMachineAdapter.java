package com.insightfullogic.honest_profiler.delivery.web;

import com.insightfullogic.honest_profiler.core.infrastructure.source.JavaVirtualMachine;
import com.insightfullogic.honest_profiler.core.infrastructure.source.VirtualMachineListener;
import org.webbitserver.WebSocketConnection;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class VirtualMachineAdapter implements VirtualMachineListener, Consumer<WebSocketConnection> {

    private final Set<JavaVirtualMachine> machines;
    private final Connections connections;
    private final MessageEncoder messages;

    public VirtualMachineAdapter(Connections connections, MessageEncoder messages) {
        this.connections = connections;
        this.messages = messages;
        machines = new HashSet<>();
        connections.setListener(this);
    }

    @Override
    public void update(Set<JavaVirtualMachine> added, Set<JavaVirtualMachine> removed) {
        machines.removeAll(removed);
        machines.addAll(added);

        sendAll(removed, messages::removeJavaVirtualMachine);
        sendAll(added, messages::addJavaVirtualMachine);
    }

    private void sendAll(Set<JavaVirtualMachine> removed, Function<JavaVirtualMachine, String> messageFactory) {
        removed.stream()
               .map(messageFactory)
               .forEach(connections::sendAll);
    }

    @Override
    public void accept(WebSocketConnection connection) {
        machines.forEach(machine -> {
            connection.send(messages.addJavaVirtualMachine(machine));
        });
    }

}
