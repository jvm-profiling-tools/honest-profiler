package com.insightfullogic.honest_profiler.delivery.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insightfullogic.honest_profiler.core.ProfileListener;
import com.insightfullogic.honest_profiler.core.collector.Profile;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import org.slf4j.Logger;

public class ProfileAdapter implements ProfileListener {

    private final ObjectMapper mapper = new ObjectMapper();

    private final Logger logger;
    private final VirtualMachine machine;
    private final ClientConnections clients;

    public ProfileAdapter(final Logger logger, final VirtualMachine machine, final ClientConnections clients) {
        this.logger = logger;
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
            logger.error(e.getMessage(), e);
        }
    }

}
