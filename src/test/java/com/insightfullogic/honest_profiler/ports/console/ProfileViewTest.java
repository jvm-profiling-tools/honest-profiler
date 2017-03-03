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
import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.core.profiles.ProfileNode;
import com.insightfullogic.honest_profiler.core.profiles.ProfileTree;
import com.insightfullogic.honest_profiler.testing_utilities.ProfileFixtures;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;

public class ProfileViewTest
{

    private final FakeConsole console = new FakeConsole();
    private final ProfileView ui = new ProfileView(console);

    @Test
    public void rendersSingleMethod()
    {
        ProfileNode root = new ProfileNode(ProfileFixtures.printf, 1, 1);
        Profile profile = new Profile(2, asList(new FlatProfileEntry(ProfileFixtures.printf, 1, 0, 1)), asList(new FlatProfileEntry(ProfileFixtures.printf, 1, 0, 1)), toTrees(root));

        ui.accept(profile);

        console.isShowingTraces(2);
        console.displaysMethod(ProfileFixtures.printf);
        console.outputContains("100.0");
    }

    private List<ProfileTree> toTrees(ProfileNode root)
    {
        return asList(new ProfileTree(0L, root, 1));
    }

    @Test
    public void rendersMultiBranchProfile()
    {
        ProfileNode left = new ProfileNode(ProfileFixtures.println, 1, 2);
        ProfileNode right = new ProfileNode(ProfileFixtures.append, 1, 2);
        ProfileNode root = new ProfileNode(ProfileFixtures.printf, 1, 1, asList(left, right));
        Profile profile = new Profile(2, asList(new FlatProfileEntry(ProfileFixtures.printf, 1, 0 , 1)), asList(new FlatProfileEntry(ProfileFixtures.printf, 1, 0 , 1)), toTrees(root));

        ui.accept(profile);

        console.isShowingTraces(2);
        console.displaysMethod(ProfileFixtures.printf);
        console.displaysMethod(ProfileFixtures.append);
        console.displaysMethod(ProfileFixtures.println);
        console.outputContains("50.0");
        console.outputContains("100.0");
    }

}
