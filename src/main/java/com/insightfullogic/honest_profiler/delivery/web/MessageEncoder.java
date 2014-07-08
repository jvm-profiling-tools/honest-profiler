package com.insightfullogic.honest_profiler.delivery.web;

import com.insightfullogic.honest_profiler.core.infrastructure.source.JavaVirtualMachine;

public class MessageEncoder {

    public String addJavaVirtualMachine(JavaVirtualMachine machine) {
        return String.format(
                "{ \"type\": \"addJvm\", \"machine\": { \"name\": \"%s\", \"id\": \"%s\" } }",
                machine.getDisplayName(),
                machine.getId());
    }

    public String removeJavaVirtualMachine(JavaVirtualMachine machine) {
        return String.format("{ \"type\": \"removeJvm\", \"id\": \"%s\" }", machine.getId());
    }

}
