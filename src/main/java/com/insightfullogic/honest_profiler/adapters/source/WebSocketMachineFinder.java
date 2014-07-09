package com.insightfullogic.honest_profiler.adapters.source;

import com.insightfullogic.honest_profiler.model.machines.MachineFinder;
import com.insightfullogic.honest_profiler.model.machines.MachineListener;
import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebSocketConnection;

public class WebSocketMachineFinder extends BaseWebSocketHandler implements MachineFinder {

    public WebSocketMachineFinder() {
    }

    @Override
    public void onOpen(WebSocketConnection connection) {

    }

    @Override
    public void onClose(WebSocketConnection connection) {

    }

    @Override
    public void onMessage(WebSocketConnection connection, byte[] message) {

    }

    @Override
    public void poll(MachineListener listener) {
        
    }
}
