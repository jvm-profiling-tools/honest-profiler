package com.insightfullogic.honest_profiler.core.conductor;

import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;

public interface MachineListener {

    public void add(VirtualMachine machine);

    public void remove(VirtualMachine machine);

}
