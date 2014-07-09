package com.insightfullogic.honest_profiler.model.machines;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class MachineFindingAgent {

    private final MachineListener listener;
    private final MachineFinder service;

    private Thread thread;

    public MachineFindingAgent(MachineListener listener, MachineFinder service) {
        this.listener = listener;
        this.service = service;
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
            while (!Thread.currentThread().isInterrupted()) {
                service.poll(listener);

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
