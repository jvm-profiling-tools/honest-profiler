package com.insightfullogic.honest_profiler.log;

import java.io.File;

public class ReaderThread extends Thread {

    private final LogParser parser;
    private final File file;
    private volatile boolean stopped;

    public ReaderThread(LogParser parser, File file) {
        this.parser = parser;
        this.file = file;
    }

    @Override
    public void run() {
        stopped = false;
        try {
            parser.parse(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        stopped = true;
    }

    public boolean hasStopped() {
        return stopped;
    }

}
