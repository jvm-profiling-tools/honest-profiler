package com.insightfullogic.honest_profiler.discovery;

import com.sun.tools.attach.VirtualMachineDescriptor;

import java.util.Set;

public interface VirtualMachineListener {

    public void update(Set<JavaVirtualMachine> added, Set<JavaVirtualMachine> removed);

}
