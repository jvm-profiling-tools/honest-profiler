package com.insightfullogic.examples;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class LargeStacktracesExample implements Runnable {

    public static void main(String[] args) throws Exception {
        int processors = Runtime.getRuntime().availableProcessors() * 2;

        ExecutorService threadPool = Executors.newFixedThreadPool(processors);
        IntStream.range(0, processors)
                 .forEach(x -> threadPool.submit(new LargeStacktracesExample()));
    }

    @Override
    public void run() {
        while (true) {
            String value = null;
            for (int i = 0; i < 100_000; i++) {
                value = someSillyMethod();
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(value);
        }
    }

    private String someSillyMethod() {
        Calendar cal = Calendar.getInstance();
        return cal.toString();
    }

}
