package com.insightfullogic.honest_profiler.delivery.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insightfullogic.honest_profiler.core.collector.Profile;
import com.insightfullogic.honest_profiler.core.ProfileListener;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;

public class ProfileAdapter implements ProfileListener {

    private final ObjectMapper mapper = new ObjectMapper();

    private final VirtualMachine machine;
    private final ClientConnections clients;

    public ProfileAdapter(VirtualMachine machine, ClientConnections clients) {
        this.machine = machine;
        this.clients = clients;
    }

    @Override
    public void accept(Profile profile) {
        try {
            NewProfileMessage newProfile = new NewProfileMessage(machine.getId(), profile);
            String message = mapper.writeValueAsString(newProfile);
            clients.sendAll(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
