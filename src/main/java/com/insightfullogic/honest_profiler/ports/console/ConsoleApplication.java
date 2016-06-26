/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
package com.insightfullogic.honest_profiler.ports.console;

import com.insightfullogic.honest_profiler.core.Monitor;
import com.insightfullogic.honest_profiler.core.filters.ProfileFilter;
import com.insightfullogic.honest_profiler.core.profiles.ProfileListener;
import com.insightfullogic.honest_profiler.ports.sources.FileLogSource;
import com.insightfullogic.honest_profiler.ports.sources.LocalMachineSource;
import org.fusesource.jansi.AnsiConsole;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ConsoleApplication
{

    private final ProfileView ui;
    private final Console output;
    private final Console error;

    private File logLocation;
    private String filterDescription;

    public ConsoleApplication(final Console error, final Console output)
    {
        this.output = output;
        this.error = error;
        ui = new ProfileView(output);
    }

    public static void main(String[] args)
    {
        AnsiConsole.systemInstall();
        ConsoleApplication entry = new ConsoleApplication(() -> System.err, () -> System.out);
        CmdLineParser parser = new CmdLineParser(entry);

        try
        {
            parser.parseArgument(args);
            entry.run();
        }
        catch (CmdLineException e)
        {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
    }

    @Option(name = "-log", usage = "set the log that you want to parser or use")
    public void setLogLocation(String logLocation)
    {
        setLogLocation(new File(logLocation));
    }

    public void setLogLocation(File logLocation)
    {
        this.logLocation = logLocation;
    }

    @Option(name = "-filter", usage = "set the filter to apply to commandline output")
    public void setFilterDescription(String filterDescription)
    {
        this.filterDescription = filterDescription;
    }

    @Option(name = "-format", usage = "set the output format, either 'flat_by_line', 'flat_by_method' or 'tree'")
    public void setProfileFormat(String profileFormat)
    {
        ProfileFormat format = ProfileFormat.valueOf(profileFormat.toUpperCase());
        if (format == null)
        {
            throw new IllegalArgumentException("Invalid argument: " + profileFormat);
        }
        ui.setProfileFormat(format);
    }

    public void run()
    {
        if (hasLogToDisplay())
        {
            displayLogFile();
        }
        else
        {
            Terminal terminal = new Terminal(System.in, System.out, () -> System.exit(0));
            terminal.display(new MachinePicker(terminal, listener ->
                new LocalMachineSource(LoggerFactory.getLogger(LocalMachineSource.class), listener)));
            terminal.run();
        }
    }

    private boolean hasLogToDisplay()
    {
        return logLocation != null;
    }

    private void displayLogFile()
    {
        try
        {
            if (!logLocation.exists() || !logLocation.canRead())
            {
                error.stream().println("Unable to find log file at: " + logLocation);
                return;
            }

            ProfileListener listener = ui;

            if (filterDescription != null)
            {
                ProfileFilter filter = new ProfileFilter();
                filter.updateFilters(filterDescription);
                listener = profile -> {
                    filter.accept(profile);
                    ui.accept(profile);
                };
            }

            output.stream().println("Printing Profile for: " + logLocation.getAbsolutePath());

            Monitor.consumeFile(new FileLogSource(logLocation), listener);
        }
        catch (Exception e)
        {
            // TODO: better error handling
            e.printStackTrace(error.stream());
        }
    }

}
