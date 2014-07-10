package com.insightfullogic.honest_profiler.core.conductor;

import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;

import java.util.Set;

public interface MachineListener {

    public void update(Set<VirtualMachine> added, Set<VirtualMachine> removed);

}
