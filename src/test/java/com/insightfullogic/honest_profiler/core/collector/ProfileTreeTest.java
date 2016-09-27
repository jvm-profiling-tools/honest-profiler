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
package com.insightfullogic.honest_profiler.core.collector;

import com.insightfullogic.honest_profiler.core.parser.Method;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;
import com.insightfullogic.honest_profiler.core.parser.TraceStart;
import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.core.profiles.ProfileNode;
import com.insightfullogic.honest_profiler.core.profiles.ProfileTree;
import com.insightfullogic.honest_profiler.testing_utilities.ProfileFixtures;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ProfileTreeTest
{

    private final FakeProfileListener listener = new FakeProfileListener();
    private final LogCollector collector = new LogCollector(listener, false);

    @Test
    public void rendersSingleNode()
    {
        collector.handle(new TraceStart(1, 1, 0L, 0L));
        collector.handle(new StackFrame(20, ProfileFixtures.printlnId));
        collector.handle(ProfileFixtures.println);
        collector.endOfLog();

        ProfileNode node = assertProfileHasSingleTree();
        assertEquals(0L, node.children().count());
        assertNode(ProfileFixtures.println, 1.0, node);
    }

    @Test
    public void collectsTwoNodesIntoATree()
    {
        printlnCallingAppend(1);
        collector.endOfLog();

        ProfileNode node = assertProfileHasSingleTree();
        assertEquals(1L, node.children().count());
        assertNode(ProfileFixtures.println, 1.0, node);
        assertNode(ProfileFixtures.append, 1.0, node.children().findFirst().get());
    }

    @Test
    public void collectsSplitMethods()
    {
        printlnCallingPrintfAndAppend(1);
        collector.endOfLog();

        ProfileNode node = assertProfileHasSingleTree();
        assertPrintlnCallingAppendAndPrintf(node);
    }

    @Test
    public void collectsFromMultipleThreads()
    {
        printlnCallingPrintfAndAppend(1);
        printlnCallingPrintfAndAppend(2);
        collector.endOfLog();

        List<ProfileTree> trees = getTrees();
        assertEquals(2, trees.size());
        trees.forEach(tree -> assertPrintlnCallingAppendAndPrintf(tree.getRootNode()));
    }

    @Test
    public void sortsSiblingsWithinTreeView()
    {
        printlnCallingAppend(1);
        printlnCallingPrintf(1);
        printlnCallingPrintf(1);
        collector.endOfLog();

        ProfileNode rootNode = getTrees().get(0).getRootNode();
        assertNode(ProfileFixtures.println, 1.0, rootNode);

        List<ProfileNode> children = rootNode.getChildren();
        assertNode(ProfileFixtures.printf, 2.0 / 3, children.get(0));
        assertNode(ProfileFixtures.append, 1.0 / 3, children.get(1));
    }

    private List<ProfileTree> getTrees()
    {
        return listener.getProfile().getTrees();
    }

    private void assertPrintlnCallingAppendAndPrintf(ProfileNode node)
    {
        assertEquals(2L, node.children().count());
        assertNode(ProfileFixtures.println, 1.0, node);

        List<ProfileNode> children = node.getChildren();
        assertNode(ProfileFixtures.append, 0.5, children.get(0));
        assertNode(ProfileFixtures.printf, 0.5, children.get(1));
    }

    private void printlnCallingPrintfAndAppend(int threadId)
    {
        printlnCallingAppend(threadId);
        printlnCallingPrintf(threadId);
    }

    private void printlnCallingPrintf(final int threadId)
    {
        collector.handle(new TraceStart(2, threadId, 0, 0));
        collector.handle(new StackFrame(20, ProfileFixtures.printfId));
        collector.handle(ProfileFixtures.printf);
        collector.handle(new StackFrame(20, ProfileFixtures.printlnId));
    }

    private void printlnCallingAppend(int threadId)
    {
        collector.handle(new TraceStart(2, threadId, 0, 0));
        collector.handle(new StackFrame(20, ProfileFixtures.appendId));
        collector.handle(ProfileFixtures.append);
        collector.handle(new StackFrame(20, ProfileFixtures.printlnId));
        collector.handle(ProfileFixtures.println);
    }

    private ProfileNode assertProfileHasSingleTree()
    {
        Profile profile = listener.getProfile();
        List<ProfileTree> trees = profile.getTrees();
        assertEquals(1, trees.size());

        return trees.get(0).getRootNode();
    }

    private void assertNode(Method method, double ratio, ProfileNode node)
    {
        assertEquals(ratio, node.getTotalTimeShare(), 0.00001);
        assertEquals(method, node.getFrameInfo());
    }

}
