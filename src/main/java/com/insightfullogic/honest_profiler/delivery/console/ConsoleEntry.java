package com.insightfullogic.honest_profiler.delivery.console;

import com.insightfullogic.honest_profiler.adapters.store.FileLogRepo;
import com.insightfullogic.honest_profiler.core.conductor.Conductor;

import java.io.File;
import java.io.IOException;

public class ConsoleEntry {

    private final Conductor conductor;
    private final ConsoleUserInterface ui;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Requires a log file to parse");
            System.exit(-1);
        }

        ConsoleEntry entry = new ConsoleEntry(() -> System.out);
        entry.loadLogFrom(new File(args[0]));
    }

    public ConsoleEntry(Console console) {
        ui = new ConsoleUserInterface(console);
        conductor = new Conductor(new FileLogRepo());
    }

    public void loadLogFrom(File file) {
        try {
            conductor.consumeFile(file, null, ui);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
