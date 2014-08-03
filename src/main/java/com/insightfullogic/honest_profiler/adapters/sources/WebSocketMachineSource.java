package com.insightfullogic.honest_profiler.adapters.sources;

import com.google.protobuf.InvalidProtocolBufferException;
import com.insightfullogic.honest_profiler.core.Conductor;
import com.insightfullogic.honest_profiler.core.DataConsumer;
import com.insightfullogic.honest_profiler.core.MachineListener;
import com.insightfullogic.honest_profiler.core.ProfileListener;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import org.slf4j.Logger;
import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebSocketConnection;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketMachineSource extends BaseWebSocketHandler {

    private final Logger logger;
    private final Map<WebSocketConnection, DataConsumer> machines;
    private final Conductor conductor;
    private final MachineListener listener;

    public WebSocketMachineSource(final Logger logger, final Conductor conductor, final MachineListener listener) {
        this.logger = logger;
        this.conductor = conductor;
        this.listener = listener;
        machines = new ConcurrentHashMap<>();
    }

    @Override
    public void onOpen(WebSocketConnection connection) {

    }

    @Override
    public void onClose(WebSocketConnection connection) {
        DataConsumer consumer = machines.remove(connection);
        if (consumer != null) {
            VirtualMachine machine = consumer.getMachine();
            listener.onClosedMachine(machine);
        }
    }

    @Override
    public void onMessage(WebSocketConnection connection, byte[] message) {
        if (machines.containsKey(connection)) {
            ByteBuffer buffer = ByteBuffer.wrap(message);
            DataConsumer consumer = machines.get(connection);
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
            ProfileListener profileListener = listener.onNewMachine(machine);
            if (profileListener != null) {
                DataConsumer consumer = conductor.pipeData(machine, profileListener);
                machines.put(connection, consumer);
            }
        } catch (InvalidProtocolBufferException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
