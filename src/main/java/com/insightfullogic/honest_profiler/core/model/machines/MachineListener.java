package com.insightfullogic.honest_profiler.core.model.machines;

import java.util.Set;

public interface MachineListener {

    public void update(Set<VirtualMachine> added, Set<VirtualMachine> removed);

}
