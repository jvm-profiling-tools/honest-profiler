package com.insightfullogic.honest_profiler.delivery.console;

import com.insightfullogic.honest_profiler.core.ProfileListener;
import com.insightfullogic.honest_profiler.core.collector.Profile;

import java.io.PrintStream;

import static com.insightfullogic.honest_profiler.delivery.console.ProfileFormat.BOTH;

public class ConsoleUserInterface implements ProfileListener {

    private final Console console;

    private ProfileFormat profileFormat = BOTH;

    public ConsoleUserInterface(Console console) {
        this.console = console;
    }

    @Override
    public void accept(Profile profile) {
        PrintStream out = console.stream();
        printHeader(profile, out);
        out.println();
        profileFormat.printProfile(profile, out);
        out.println();
    }

    private void printHeader(Profile profile, PrintStream out) {
        out.print("Number of stack traces: ");
        out.print(Integer.toString(profile.getTraceCount()));
    }

    public void setProfileFormat(ProfileFormat profileFormat) {
        this.profileFormat = profileFormat;
    }

}
