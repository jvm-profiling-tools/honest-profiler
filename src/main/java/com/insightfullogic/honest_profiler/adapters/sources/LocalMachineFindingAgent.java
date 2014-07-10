package com.insightfullogic.honest_profiler.adapters.sources;

import com.insightfullogic.honest_profiler.core.conductor.MachineListener;
import com.insightfullogic.honest_profiler.core.sources.MachineSource;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

// TODO: combine this and LocalMachineSource into one class
public class LocalMachineFindingAgent {

    private final MachineListener listener;
    private final List<MachineSource> finders;

    private Thread thread;

    public LocalMachineFindingAgent(MachineListener listener, List<MachineSource> finders) {
        this.listener = listener;
        this.finders = finders;
    }

    @PostConstruct
    public void start() {
        thread = new Thread(this::discoverVirtualMachines);
        thread.setDaemon(true);
        thread.start();
    }

    public void discoverVirtualMachines() {
        System.out.println("Started");
        try {
            while (!Thread.currentThread().isInterrupted()) {
                finders.forEach(finder -> finder.poll(listener));

                sleep();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        thread.interrupt();
    }

}
