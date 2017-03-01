package com.insightfullogic.honest_profiler.testing_utilities;

import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;

import java.io.File;

public class VirtualMachineFixtures
{
    private VirtualMachineFixtures(){ }
    
    public static final VirtualMachine vmNoAgent = new VirtualMachine("0", "vm without agent", false, "", "");
    public static final VirtualMachine vmWithAgent = new VirtualMachine("1", "vm with agent", true, new File(".").getAbsolutePath(), "foobar-agentpath=blahblahlogPath=" + new File(".").getAbsolutePath());

}
