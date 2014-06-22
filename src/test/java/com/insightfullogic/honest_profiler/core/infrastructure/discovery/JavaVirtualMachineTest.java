package com.insightfullogic.honest_profiler.core.infrastructure.discovery;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import org.junit.Test;

import java.lang.management.ManagementFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class JavaVirtualMachineTest {

    @Test
    public void understandsVmName() {
        VirtualMachineDescriptor thisVM = findThisVm();
        JavaVirtualMachine jvm = new JavaVirtualMachine(thisVM);
        assertEquals(thisVM.id(), jvm.getId());
        assertEquals(thisVM.displayName(), jvm.getDisplayName());
        assertFalse(jvm.isAgentLoaded());
    }

    private VirtualMachineDescriptor findThisVm() {
        String[] pidAndHost = ManagementFactory.getRuntimeMXBean().getName().split("@");
        String pid = pidAndHost[0];
        return VirtualMachine.list()
                             .stream()
                             .filter(vm -> pid.equals(vm.id()))
                             .findFirst()
                             .orElseThrow(IllegalArgumentException::new);
    }

}
