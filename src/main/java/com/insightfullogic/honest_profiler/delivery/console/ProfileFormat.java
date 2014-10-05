/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 **/
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
