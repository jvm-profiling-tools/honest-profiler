package com.insightfullogic.honest_profiler.adapters.source;

import com.insightfullogic.honest_profiler.model.machines.VirtualMachine;
import com.insightfullogic.honest_profiler.model.machines.MachineFinder;
import com.insightfullogic.honest_profiler.model.machines.MachineListener;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class LocalMachineFinder implements MachineFinder {

    private static final String VM_ARGS = "sun.jvm.args";
    private static final String AGENT_NAME = "liblagent.so";
    private static final String USER_DIR = "user.dir";

    private Set<VirtualMachineDescriptor> previous = new HashSet<>();

    @Override
    public void poll(MachineListener listener) {
        Set<VirtualMachineDescriptor> current = new HashSet<>(com.sun.tools.attach.VirtualMachine.list());
        listener.update(difference(current, previous), difference(previous, current));
        previous = current;
    }

    private Set<VirtualMachine> difference(Set<VirtualMachineDescriptor> left, Set<VirtualMachineDescriptor> right) {
        return left.stream()
                   .filter(vm -> !right.contains(vm))
                   .flatMap(this::attach)
                   .collect(toSet());
    }

    private Stream<VirtualMachine> attach(VirtualMachineDescriptor vmDescriptor) {
        try {
            com.sun.tools.attach.VirtualMachine vm = com.sun.tools.attach.VirtualMachine.attach(vmDescriptor);
            Properties agentProperties = vm.getAgentProperties();
            String vmArgs = agentProperties.getProperty(VM_ARGS);

            String id = vmDescriptor.id();
            String displayName = vmDescriptor.displayName();
            boolean agentLoaded = vmArgs.contains(AGENT_NAME);
            String userDir = agentProperties.getProperty(USER_DIR);

            return Stream.of(new VirtualMachine(id, displayName, agentLoaded, userDir));
        } catch (AttachNotSupportedException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            if (!noSuchProcess(e)) {
                e.printStackTrace();
            }
        }
        return Stream.empty();
    }

    private boolean noSuchProcess(IOException e) {
        return e.getMessage().contains("No such process");
    }

}
