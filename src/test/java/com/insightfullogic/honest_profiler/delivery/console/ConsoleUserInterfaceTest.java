package com.insightfullogic.honest_profiler.delivery.console;

import com.insightfullogic.honest_profiler.ProfileFixtures;
import com.insightfullogic.honest_profiler.core.collector.FlatProfileEntry;
import com.insightfullogic.honest_profiler.core.collector.Profile;
import com.insightfullogic.honest_profiler.core.collector.ProfileNode;
import com.insightfullogic.honest_profiler.core.collector.ProfileTree;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;

public class ConsoleUserInterfaceTest {

    private final FakeConsole console = new FakeConsole();
    private final ConsoleUserInterface ui = new ConsoleUserInterface(console);

    @Test
    public void rendersSingleMethod() {
        ProfileNode root = new ProfileNode(ProfileFixtures.printf, 1.0);
        Profile profile = new Profile(2, asList(new FlatProfileEntry(ProfileFixtures.printf, 1.0, 0.0)), toTrees(root));

        ui.accept(profile);

        console.isShowingTraces(2);
        console.displaysMethod(ProfileFixtures.printf);
        console.outputContains("1.0");
    }

    private List<ProfileTree> toTrees(ProfileNode root) {
        return asList(new ProfileTree(root));
    }

    @Test
    public void rendersMultiBranchProfile() {
        ProfileNode left = new ProfileNode(ProfileFixtures.println, 0.5);
        ProfileNode right = new ProfileNode(ProfileFixtures.append, 0.5);
        ProfileNode root = new ProfileNode(ProfileFixtures.printf, 1, asList(left, right));
        Profile profile = new Profile(2, asList(new FlatProfileEntry(ProfileFixtures.printf, 1.0, 0.0)), toTrees(root));

        ui.accept(profile);

        console.isShowingTraces(2);
        console.displaysMethod(ProfileFixtures.printf);
        console.displaysMethod(ProfileFixtures.append);
        console.displaysMethod(ProfileFixtures.println);
        console.outputContains("0.5");
        console.outputContains("1.0");
    }

}
