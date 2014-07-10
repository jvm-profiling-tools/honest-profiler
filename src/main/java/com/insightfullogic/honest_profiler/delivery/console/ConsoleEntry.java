package com.insightfullogic.honest_profiler.delivery.console;

import com.insightfullogic.honest_profiler.core.collector.LogCollector;
import com.insightfullogic.honest_profiler.core.parser.LogParser;

import java.io.File;

public class ConsoleEntry {

    private final LogCollector collector;
    private final LogParser parser;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Requires a log file to parse");
            System.exit(-1);
        }

        ConsoleEntry entry = new ConsoleEntry(() -> System.out);
        entry.loadLogFrom(new File(args[0]));
    }

    public ConsoleEntry(Console console) {
        ConsoleUserInterface ui = new ConsoleUserInterface(console);
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
