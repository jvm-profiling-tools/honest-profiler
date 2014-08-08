package com.insightfullogic.honest_profiler.delivery.console;

import com.insightfullogic.honest_profiler.adapters.store.FileLogRepo;
import com.insightfullogic.honest_profiler.core.Conductor;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class ConsoleEntry {

    private final Conductor conductor;
    private final ConsoleUserInterface ui;
    private final Logger logger;

    private File logLocation;
    private String filter;

    public static void main(String[] args) {
        ConsoleEntry entry = new ConsoleEntry(LoggerFactory.getLogger(ConsoleEntry.class), () -> System.out);
        CmdLineParser parser = new CmdLineParser(entry);

        try {
            parser.parseArgument(args);
            entry.run();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
    }

    public ConsoleEntry(final Logger logger, final Console console) {
        this.logger = logger;
        ui = new ConsoleUserInterface(console);
        conductor = new Conductor(new FileLogRepo());
    }

    @Option(name = "-log", usage = "set the log that you want to parser or use", required = true)
    public void setLogLocation(String logLocation) {
        setLogLocation(new File(logLocation));
    }

    public void setLogLocation(File logLocation) {
        this.logLocation = logLocation;
    }

    @Option(name = "-filter", usage = "set the filter to apply to commandline output")
    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void run() {
        try {
            conductor.consumeFile(logLocation, null, ui);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
