package com.insightfullogic.honest_profiler.user_interface.console;

import com.insightfullogic.honest_profiler.Console;
import com.insightfullogic.honest_profiler.collector.Profile;
import com.insightfullogic.honest_profiler.collector.ProfileTreeNode;
import com.insightfullogic.honest_profiler.log.Method;
import com.insightfullogic.honest_profiler.user_interface.UserInterface;

import java.io.PrintStream;
import java.util.stream.IntStream;

public class ConsoleUserInterface implements UserInterface {

    private final Console console;

    public ConsoleUserInterface(Console console) {
        this.console = console;
    }

    @Override
    public void accept(Profile profile) {
        PrintStream out = console.out();
        printHeader(profile, out);
        printFlatProfile(profile, out);
        printTreeProfile(profile);
    }

    private void printHeader(Profile profile, PrintStream out) {
        out.print("Number of stack traces: ");
        out.print(Integer.toString(profile.getTraceCount()));
    }

    private void printFlatProfile(Profile profile, PrintStream out) {
        out.append("\n\nFlat Profile:");
        profile.flatProfile().forEach(entry -> {
            Method method = entry.getMethod();
            double timeShare = entry.getTimeShare();
            out.print("\n\t");
            printMethod(method, timeShare);
        });
    }

    private void printMethod(Method method, double timeShare) {
        console.out().printf("%.2f %s.%s", timeShare, method.getClassName(), method.getMethodName());
    }

    private void printTreeProfile(Profile profile) {
        console.out().print("\n\nTree Profile:");
        profile.getTrees().forEach(tree -> printNode(tree.getRootNode(), 1));
    }

    private void printNode(ProfileTreeNode node, int depth) {
        PrintStream out = console.out();
        out.print('\n');

        IntStream.range(0, depth).forEach(i -> out.print('\t'));
        printMethod(node.getMethod(), node.getTimeShare());

        int childDepth = depth + 1;
        node.children().forEach(child -> printNode(child, childDepth));
    }

}
