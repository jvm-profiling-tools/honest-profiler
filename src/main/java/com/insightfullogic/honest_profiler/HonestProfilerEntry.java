package com.insightfullogic.honest_profiler;

import com.insightfullogic.honest_profiler.collector.LogCollector;
import com.insightfullogic.honest_profiler.log.LogParser;
import com.insightfullogic.honest_profiler.user_interface.UserInterface;
import com.insightfullogic.honest_profiler.user_interface.console.ConsoleUserInterface;

import java.io.File;

public class HonestProfilerEntry {

    private final LogParser parser;
    private final LogCollector collector;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Requires a log file to parse");
            System.exit(-1);
        }

        HonestProfilerEntry entry = new HonestProfilerEntry(() -> System.out);
        entry.loadLogFrom(new File(args[0]));
    }

    public HonestProfilerEntry(Console console) {
        UserInterface ui = new ConsoleUserInterface(console);
        collector = new LogCollector(ui);
        parser = new LogParser(collector);
    }

    public boolean hasLoadedLog() {
        return collector.isLogComplete();
    }

    public void loadLogFrom(File file) {
        parser.parse(file);
    }

}
