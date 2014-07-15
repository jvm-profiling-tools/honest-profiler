package com.insightfullogic.honest_profiler.core.conductor;

import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;

public interface MachineListener {

    // TODO: decouple listening to machines from listening to profiles
    ProfileListener onNewMachine(VirtualMachine machine);

    void onClosedMachine(VirtualMachine machine);

}
