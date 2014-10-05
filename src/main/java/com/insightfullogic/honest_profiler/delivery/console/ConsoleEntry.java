package com.insightfullogic.honest_profiler.delivery.console;

import com.insightfullogic.honest_profiler.adapters.store.FileLogRepo;
import com.insightfullogic.honest_profiler.core.Conductor;
import com.insightfullogic.honest_profiler.core.ProfileListener;
import com.insightfullogic.honest_profiler.core.filters.ProfileFilter;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.IOException;

public class ConsoleEntry {

    private final Conductor conductor;
    private final ConsoleUserInterface ui;
    private final Console output;
    private final Console error;

    private File logLocation;
    private String filterDescription;

    public static void main(String[] args) {
        ConsoleEntry entry = new ConsoleEntry(() -> System.err, () -> System.out);
        CmdLineParser parser = new CmdLineParser(entry);

        try {
            parser.parseArgument(args);
            entry.run();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
    }

    public ConsoleEntry(final Console error, final Console output) {
        this.output = output;
        this.error = error;
        ui = new ConsoleUserInterface(output);
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
    public void setFilterDescription(String filterDescription) {
        this.filterDescription = filterDescription;
    }

    @Option(name = "-format", usage = "set the output format, either 'flat' or 'tree'")
    public void setProfileFormat(String profileFormat) {
        ProfileFormat format = ProfileFormat.valueOf(profileFormat.toUpperCase());
        if (format == null) {
            throw new IllegalArgumentException("Invalid argument: " + profileFormat);
        }
        ui.setProfileFormat(format);
    }

    public void run() {
        try {
            if (!logLocation.exists() || !logLocation.canRead()) {
                error.stream().println("Unable to find log file at: " + logLocation);
                return;
            }

            ProfileListener listener = ui;

            if (filterDescription != null) {
                ProfileFilter filter = new ProfileFilter();
                filter.updateFilters(filterDescription);
                listener = profile -> {
                    filter.accept(profile);
                    ui.accept(profile);
                };
            }

            output.stream().println("Printing Profile for: " + logLocation.getAbsolutePath());

            conductor.consumeFile(logLocation, null, listener);
        } catch (IOException e) {
            e.printStackTrace(error.stream());
        }
    }

}
