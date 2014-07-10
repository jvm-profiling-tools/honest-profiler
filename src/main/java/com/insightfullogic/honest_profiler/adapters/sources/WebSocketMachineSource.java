package com.insightfullogic.honest_profiler.adapters.sources;

import com.google.protobuf.InvalidProtocolBufferException;
import com.insightfullogic.honest_profiler.core.conductor.Conductor;
import com.insightfullogic.honest_profiler.core.conductor.LogConsumer;
import com.insightfullogic.honest_profiler.core.conductor.MachineListener;
import com.insightfullogic.honest_profiler.core.conductor.ProfileListener;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebSocketConnection;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketMachineSource extends BaseWebSocketHandler {

    private final Map<WebSocketConnection, LogConsumer> machines;
    private final Conductor conductor;
    private final MachineListener listener;

    public WebSocketMachineSource(Conductor conductor, MachineListener listener) {
        this.conductor = conductor;
        this.listener = listener;
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
            listener.remove(machine);
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
            ProfileListener profileListener = listener.add(machine);
            LogConsumer consumer = conductor.onNewLog(machine, profileListener);
            machines.put(connection, consumer);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

}
