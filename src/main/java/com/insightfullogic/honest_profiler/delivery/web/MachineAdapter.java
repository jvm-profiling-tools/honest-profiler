package com.insightfullogic.honest_profiler.delivery.web;

import com.insightfullogic.honest_profiler.model.machines.VirtualMachine;
import com.insightfullogic.honest_profiler.model.machines.MachineListener;
import org.webbitserver.WebSocketConnection;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class MachineAdapter implements MachineListener, Consumer<WebSocketConnection> {

    private final Set<VirtualMachine> machines;
    private final Connections connections;
    private final MessageEncoder messages;

    public MachineAdapter(Connections connections, MessageEncoder messages) {
        this.connections = connections;
        this.messages = messages;
        machines = new HashSet<>();
        connections.setListener(this);
    }

    @Override
    public void update(Set<VirtualMachine> added, Set<VirtualMachine> removed) {
        machines.removeAll(removed);
        machines.addAll(added);

        sendAll(removed, messages::removeJavaVirtualMachine);
        sendAll(added, messages::addJavaVirtualMachine);
    }

    private void sendAll(Set<VirtualMachine> removed, Function<VirtualMachine, String> messageFactory) {
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
