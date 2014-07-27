package com.insightfullogic.examples;

public class SleepingThreadExample {

    public static void main(String[] args) throws Exception {
        while (true) {
            Thread.sleep(100);
            subMethod();
        }
    }

    private static void subMethod() {
        System.out.println("calling some code, lalala");
    }

}
