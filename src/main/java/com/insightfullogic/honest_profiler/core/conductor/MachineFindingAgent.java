package com.insightfullogic.honest_profiler.core.conductor;

import com.insightfullogic.honest_profiler.core.sources.MachineSource;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

public class MachineFindingAgent {

    private final MachineListener listener;
    private final List<MachineSource> finders;

    private Thread thread;

    public MachineFindingAgent(MachineListener listener, List<MachineSource> finders) {
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
