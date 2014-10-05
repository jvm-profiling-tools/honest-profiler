package com.insightfullogic.honest_profiler.delivery.console;

import com.insightfullogic.honest_profiler.core.collector.Profile;
import com.insightfullogic.honest_profiler.core.collector.ProfileNode;
import com.insightfullogic.honest_profiler.core.parser.Method;

import java.io.PrintStream;
import java.util.stream.IntStream;

/**
 * .
 */
public enum ProfileFormat {

    FLAT {
        @Override
        public void printProfile(Profile profile, PrintStream out) {
            out.append("\n\nFlat Profile:");
            profile.flatProfile().forEach(entry -> {
                Method method = entry.getMethod();
                double timeShare = entry.getTotalTimeShare();
                out.print("\n\t");
                printMethod(method, timeShare, out);
            });
        }
    },

    TREE {
        @Override
        public void printProfile(Profile profile, PrintStream out) {
            out.print("\n\nTree Profile:");
            profile.getTrees().forEach(tree -> printNode(tree.getRootNode(), 1, out));
        }
    },

    BOTH {
        @Override
        public void printProfile(Profile profile, PrintStream out) {
            FLAT.printProfile(profile, out);
            TREE.printProfile(profile, out);
        }
    };

    public abstract void printProfile(Profile profile, PrintStream out);

    void printMethod(Method method, double timeShare, PrintStream out) {
        if (method != null)
            out.printf("%.2f %s.%s", timeShare, method.getClassName(), method.getMethodName());
    }

    void printNode(ProfileNode node, int depth, PrintStream out) {
        out.print('\n');

        IntStream.range(0, depth).forEach(i -> out.print("  "));
        printMethod(node.getMethod(), node.getTotalTimeShare(), out);

        int childDepth = depth + 1;
        node.children().forEach(child -> printNode(child, childDepth, out));
    }

}
