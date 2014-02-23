package com.insightfullogic.honest_profiler.discovery;

import com.sun.tools.attach.VirtualMachineDescriptor;

import java.util.Set;

public interface VirtualMachineListener {

    public void update(Set<VirtualMachineDescriptor> added, Set<VirtualMachineDescriptor> removed);

}
