package com.insightfullogic.honest_profiler.delivery.web;

import com.insightfullogic.honest_profiler.model.machines.VirtualMachine;

public class MessageEncoder {

    public String addJavaVirtualMachine(VirtualMachine machine) {
        return String.format(
                "{ \"type\": \"addJvm\", \"machine\": { \"name\": \"%s\", \"id\": \"%s\", \"agent\": %b } }",
                machine.getDisplayName(),
                machine.getId(),
                machine.isAgentLoaded());
    }

    public String removeJavaVirtualMachine(VirtualMachine machine) {
        return String.format("{ \"type\": \"removeJvm\", \"id\": \"%s\" }", machine.getId());
    }

}
