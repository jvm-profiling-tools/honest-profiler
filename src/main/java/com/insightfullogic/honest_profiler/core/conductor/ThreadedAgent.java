package com.insightfullogic.honest_profiler.core.conductor;

public class ThreadedAgent implements Runnable {

    public static interface Block {
        public boolean run() throws Exception;
    }

    private final Block block;

    private Thread thread;

    public ThreadedAgent(Block block) {
        this.block = block;
    }

    public void start() {
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }

    @Override
    public void run() {
        System.out.println("Started");
        try {
            while (!Thread.currentThread().isInterrupted() && block.run())
                ;

        } catch (Throwable throwable) {
            // Deliberately catching throwable since we're at the top of a thread
            throwable.printStackTrace();
        }
    }

}
