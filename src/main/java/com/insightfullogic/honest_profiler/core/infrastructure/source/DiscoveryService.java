package com.insightfullogic.honest_profiler.core.infrastructure.source;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class DiscoveryService {

    private final VirtualMachineListener listener;

    private Thread thread;

    public DiscoveryService(VirtualMachineListener listener) {
        this.listener = listener;
    }

    @PostConstruct
    public void start() {
        System.out.println("Starting");
        thread = new Thread(this::discoverVirtualMachines);
        thread.setDaemon(true);
        thread.start();
    }

    public void discoverVirtualMachines() {
        try {
            Set<VirtualMachineDescriptor> previous = new HashSet<>();
            while (!Thread.currentThread().isInterrupted()) {
                Set<VirtualMachineDescriptor> current = new HashSet<>(VirtualMachine.list());
                listener.update(difference(current, previous), difference(previous, current));

                sleep();
                previous = current;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Set<JavaVirtualMachine> difference(Set<VirtualMachineDescriptor> left, Set<VirtualMachineDescriptor> right) {
        return left.stream()
                   .filter(vm -> !right.contains(vm))
                   .map(JavaVirtualMachine::new)
                   .collect(toSet());
    }

    private void sleep() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // Ignore
        }
    }

    @PreDestroy
    public void stop() {
        thread.interrupt();;
    }

}
