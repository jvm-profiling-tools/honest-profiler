package com.insightfullogic.honest_profiler.delivery.web;

import com.insightfullogic.honest_profiler.core.conductor.MachineListener;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import org.webbitserver.WebSocketConnection;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

// Not thread safe
public class MachineAdapter implements MachineListener, Consumer<WebSocketConnection> {

    private final Set<VirtualMachine> machines;
    private final ClientConnections clients;
    private final MessageEncoder messages;

    public MachineAdapter(ClientConnections clients, MessageEncoder messages) {
        this.clients = clients;
        this.messages = messages;
        machines = new HashSet<>();
        clients.setListener(this);
    }

    @Override
    public void accept(WebSocketConnection connection) {
        machines.forEach(machine -> {
            connection.send(messages.addJavaVirtualMachine(machine));
        });
    }

    @Override
    public ProfileAdapter add(VirtualMachine machine) {
        machines.add(machine);
        clients.sendAll(messages.addJavaVirtualMachine(machine));
        return new ProfileAdapter(machine, clients);
    }

    @Override
    public void remove(VirtualMachine machine) {
        machines.remove(machine);
        clients.sendAll(messages.removeJavaVirtualMachine(machine));
    }

}
