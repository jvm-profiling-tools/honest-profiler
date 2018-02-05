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

import com.insightfullogic.honest_profiler.core.collector.FlatProfileEntry;
import com.insightfullogic.honest_profiler.core.collector.Frame;
import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.core.profiles.ProfileNode;

import java.io.PrintStream;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * Formats printed output for different types of profile.
 * <p>
 * NB: The use of unnecessary StringBuilder instances rather than
 * directly calling print on PrintStream is a deliberate performance
 * optimisation to fix issue #56. If you're piping the output through
 * head the repeated calls to print cause unnecessary buffering and
 * context switching overhead. Using local buffers improves performance
 * by 6x-10x.
 */
public enum ProfileFormat
{

    FLAT_BY_METHOD
        {
            @Override
            public void printProfile(Profile profile, PrintStream out)
            {
                StringBuilder sb = new StringBuilder("\n\nFlat Profile (by method):");
                profile.flatByMethodProfile().forEach(entry -> appendFlatProfileEntry(sb, entry));
                out.print(sb);
            }
        },

    FLAT_BY_LINE
        {
            @Override
            public void printProfile(Profile profile, PrintStream out)
            {
                StringBuilder sb = new StringBuilder("\n\nFlat Profile (by line):");
                profile.flatByFrameProfile().forEach(entry -> appendFlatProfileEntry(sb, entry));
                out.print(sb);
            }
        },

    TREE
        {
            @Override
            public void printProfile(Profile profile, PrintStream out)
            {
                StringBuilder sb = new StringBuilder("\n\nTree Profile:");
                profile.getTrees().forEach(tree -> printNode(tree.getRootNode(), 1, sb));
                out.print(sb);
            }
        },

    ALL
        {
            @Override
            public void printProfile(Profile profile, PrintStream out)
            {
                FLAT_BY_METHOD.printProfile(profile, out);
                FLAT_BY_LINE.printProfile(profile, out);
                TREE.printProfile(profile, out);
            }
        };

    public abstract void printProfile(Profile profile, PrintStream out);

    static void appendFlatProfileEntry(StringBuilder sb, FlatProfileEntry entry)
    {
        Frame method = entry.getFrameInfo();
        double totalShare = entry.getTotalTimeShare();
        double selfShare = entry.getSelfTimeShare();
        sb.append("\n\t");
        printFrameInfo(method, totalShare, selfShare, sb::append);
    }

    static void printFrameInfo(Frame frameInfo, double totalShare, double selfShare, Consumer<String> out)
    {
        if (frameInfo == null)
        {
            out.accept("NULL FRAME ERR");
        }
        else if (frameInfo.getBci() == Frame.BCI_ERR_IGNORE)
        {
            out.accept(String.format("(t %4.1f,s %4.1f) %s::%s%s",
                totalShare * 100, selfShare * 100,
                frameInfo.getClassName(), frameInfo.getMethodName(), frameInfo.getMethodSignature()));
        }
        else
        {
            out.accept(String.format("(t %4.1f,s %4.1f) %s::%s%s @ (bci=%d,line=%d)",
                totalShare * 100, selfShare * 100,
                frameInfo.getClassName(), frameInfo.getMethodName(), frameInfo.getMethodSignature(),
                frameInfo.getBci(), frameInfo.getLine()));
        }
    }

    void printNode(ProfileNode node, int depth, StringBuilder out)
    {
        out.append("\n");
        IntStream.range(0, depth).forEach(ignore -> out.append(' '));
        printFrameInfo(node.getFrameInfo(), node.getTotalTimeShare(), node.getSelfTimeShare(), out::append);

        int childDepth = depth + 1;
        node.children().forEach(child -> printNode(child, childDepth, out));
    }

}
