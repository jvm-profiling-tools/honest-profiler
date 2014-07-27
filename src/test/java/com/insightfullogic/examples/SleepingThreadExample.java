package com.insightfullogic.examples;

public class SleepingThreadExample {

    public static void main(String[] args) throws Exception {
        while (true) {
            long time = System.currentTimeMillis();
            Thread.sleep(500);
            if ((System.currentTimeMillis() - time) < 500) {
                System.out.println("Sleep has been broken");
            }
            for (int i = 0; i < 1000; i++) {
                subMethod();
            }
        }
    }

    private static void subMethod() {
        System.out.println("calling some code, lalala");
    }

}
