package com.insightfullogic.honest_profiler.adapters.sources;

import com.google.protobuf.InvalidProtocolBufferException;
import com.insightfullogic.honest_profiler.core.conductor.Conductor;
import com.insightfullogic.honest_profiler.core.conductor.LogConsumer;
import com.insightfullogic.honest_profiler.core.conductor.MachineListener;
import com.insightfullogic.honest_profiler.core.sources.MachineSource;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebSocketConnection;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WebSocketMachineSource extends BaseWebSocketHandler implements MachineSource {

    private final Map<WebSocketConnection, LogConsumer> machines;
    private final Queue<VirtualMachine> added;
    private final Queue<VirtualMachine> removed;
    private final Conductor conductor;

    public WebSocketMachineSource(Conductor conductor) {
        this.conductor = conductor;
        added = new ConcurrentLinkedQueue<>();
        removed = new ConcurrentLinkedQueue<>();
        machines = new ConcurrentHashMap<>();
    }

    @Override
    public void onOpen(WebSocketConnection connection) {

    }

    @Override
    public void onClose(WebSocketConnection connection) {
        LogConsumer consumer = machines.remove(connection);
        if (consumer != null) {
            VirtualMachine machine = consumer.getMachine();
            added.remove(machine);
            add(machine, removed);
        }
    }

    @Override
    public void onMessage(WebSocketConnection connection, byte[] message) {
        if (machines.containsKey(connection)) {
            ByteBuffer buffer = ByteBuffer.wrap(message);
            LogConsumer consumer = machines.get(connection);
            if (consumer != null) {
                consumer.accept(buffer);
            }
        } else {
            newMachine(connection, message);
        }
    }

    private void newMachine(WebSocketConnection connection, byte[] message) {
        try {
            Messages.NewMachine newMachine = Messages.NewMachine.parseFrom(message);
            VirtualMachine machine = new VirtualMachine(newMachine.getId(), newMachine.getDisplayName(), true, "");
            LogConsumer consumer = conductor.onNewLog(machine);
            machines.put(connection, consumer);
            add(machine, added);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void poll(MachineListener listener) {
        listener.update(drain(added), drain(removed));
    }

    private Set<VirtualMachine> drain(Queue<VirtualMachine> queue) {
        Set<VirtualMachine> machines = new HashSet<>();
        while (!queue.isEmpty()) {
            machines.add(queue.remove());
        }
        return machines;
    }

    private void add(VirtualMachine machine, Queue<VirtualMachine> queue) {
        if (!queue.add(machine)) {
            // TODO: add backoffs and potential thread restart
            System.err.println("Dropped: " + machine);
        }
    }

}
