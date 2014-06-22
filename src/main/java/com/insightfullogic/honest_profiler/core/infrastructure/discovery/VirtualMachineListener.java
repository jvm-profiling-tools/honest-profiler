package com.insightfullogic.honest_profiler.core.infrastructure.discovery;

import java.util.Set;

public interface VirtualMachineListener {

    public void update(Set<JavaVirtualMachine> added, Set<JavaVirtualMachine> removed);

}
