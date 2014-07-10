package com.insightfullogic.honest_profiler.delivery.web;

import com.insightfullogic.honest_profiler.core.collector.Profile;
import com.insightfullogic.honest_profiler.core.conductor.ProfileListener;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;

public class ProfileAdapter implements ProfileListener {

    private final VirtualMachine machine;
    private final ClientConnections clients;

    public ProfileAdapter(VirtualMachine machine, ClientConnections clients) {
        this.machine = machine;
        this.clients = clients;
    }

    @Override
    public void accept(Profile profile) {
        // TODO: convert to json
    }

}
