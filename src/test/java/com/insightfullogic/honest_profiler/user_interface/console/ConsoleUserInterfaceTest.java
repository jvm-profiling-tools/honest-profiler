package com.insightfullogic.honest_profiler.user_interface.console;

import com.insightfullogic.honest_profiler.FakeConsole;
import com.insightfullogic.honest_profiler.ProfileFixtures;
import com.insightfullogic.honest_profiler.collector.FlatProfileEntry;
import com.insightfullogic.honest_profiler.collector.Profile;
import com.insightfullogic.honest_profiler.collector.ProfileTree;
import com.insightfullogic.honest_profiler.collector.ProfileTreeNode;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertTrue;

public class ConsoleUserInterfaceTest {

    private final FakeConsole console = new FakeConsole();
    private final ConsoleUserInterface ui = new ConsoleUserInterface(console);

    @Test
    public void rendersSingleMethod() {
        ProfileTreeNode root = new ProfileTreeNode(ProfileFixtures.printf, 1.0);
        Profile profile = new Profile(2, asList(new FlatProfileEntry(ProfileFixtures.printf, 1.0)), toTrees(root));

        ui.accept(profile);

        console.isShowingTraces(2);
        console.displaysMethod(ProfileFixtures.printf);
        console.outputContains("1.0");
    }

    private List<ProfileTree> toTrees(ProfileTreeNode root) {
        return asList(new ProfileTree(root));
    }

    @Test
    public void rendersMultiBranchProfile() {
        ProfileTreeNode left = new ProfileTreeNode(ProfileFixtures.println, 0.5);
        ProfileTreeNode right = new ProfileTreeNode(ProfileFixtures.append, 0.5);
        ProfileTreeNode root = new ProfileTreeNode(ProfileFixtures.printf, 1, asList(left, right));
        Profile profile = new Profile(2, asList(new FlatProfileEntry(ProfileFixtures.printf, 1.0)), toTrees(root));

        ui.accept(profile);

        console.isShowingTraces(2);
        console.displaysMethod(ProfileFixtures.printf);
        console.displaysMethod(ProfileFixtures.append);
        console.displaysMethod(ProfileFixtures.println);
        console.outputContains("0.5");
        console.outputContains("1.0");
    }

}
