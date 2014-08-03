package com.insightfullogic.honest_profiler.delivery.console;

import com.insightfullogic.honest_profiler.adapters.store.FileLogRepo;
import com.insightfullogic.honest_profiler.core.Conductor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class ConsoleEntry {

    private final Conductor conductor;
    private final ConsoleUserInterface ui;
    private final Logger logger;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Requires a log file to parse");
            System.exit(-1);
        }

        ConsoleEntry entry = new ConsoleEntry(LoggerFactory.getLogger(ConsoleEntry.class), () -> System.out);
        entry.loadLogFrom(new File(args[0]));
    }

    public ConsoleEntry(final Logger logger, final Console console) {
        this.logger = logger;
        ui = new ConsoleUserInterface(console);
        conductor = new Conductor(new FileLogRepo());
    }

    public void loadLogFrom(File file) {
        try {
            conductor.consumeFile(file, null, ui);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
