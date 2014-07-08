package com.insightfullogic.honest_profiler.delivery.web;

import com.insightfullogic.honest_profiler.core.infrastructure.source.JavaVirtualMachine;

public class MessageEncoder {

    public String addJavaVirtualMachine(JavaVirtualMachine machine) {
        return jvmMessage("addJvm", machine);
    }

    public String removeJavaVirtualMachine(JavaVirtualMachine machine) {
        return jvmMessage("removeJvm", machine);
    }

    private String jvmMessage(String type, JavaVirtualMachine machine) {
        return String.format(
            "{ type: %s, machine: { name: %s, id: %s } }",
            type,
            machine.getDisplayName(),
            machine.getId());
    }

}
