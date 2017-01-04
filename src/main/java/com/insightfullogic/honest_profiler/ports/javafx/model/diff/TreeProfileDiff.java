package com.insightfullogic.honest_profiler.ports.javafx.model.diff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.core.profiles.ProfileTree;

public class TreeProfileDiff
{
    private List<ThreadNodeDiff> threads;
    private Map<String, ThreadNodeDiff> threadsByName;

    public TreeProfileDiff()
    {
        threads = new ArrayList<>();
        threadsByName = new HashMap<>();
    }

    public TreeProfileDiff(Profile baseProfile, Profile newProfile)
    {
        threads = new ArrayList<>();
        threadsByName = new HashMap<>();

        if (baseProfile != null)
        {
            updateForBase(baseProfile);
        }

        if (newProfile != null)
        {
            updateForNew(newProfile);
        }
    }

    public void clear()
    {
        threads.clear();
        threadsByName.clear();
    }

    public List<ThreadNodeDiff> getChildren()
    {
        return threads;
    }

    public TreeProfileDiff updateForBase(Profile profile)
    {
        profile.getTrees().forEach(tree -> threadsByName.compute(
            nameForThread(tree),
            (threadName, diff) -> diff == null ? newDiff(tree, true)
                : diff.updateForBase(tree))
        );
        return this;
    }

    public TreeProfileDiff updateForNew(Profile profile)
    {
        profile.getTrees().forEach(tree -> threadsByName.compute(
            nameForThread(tree),
            (threadName, diff) -> diff == null ? newDiff(tree, false)
                : diff.updateForNew(tree))
        );
        return this;
    }

    private ThreadNodeDiff newDiff(ProfileTree tree, boolean base)
    {
        ThreadNodeDiff result = base ? new ThreadNodeDiff(tree, null)
            : new ThreadNodeDiff(null, tree);
        threads.add(result);
        return result;
    }

    private String nameForThread(ProfileTree tree)
    {
        return tree.getThreadName() == null || tree.getThreadName().isEmpty() ? "<UNKNOWN>"
            : tree.getThreadName();
    }
}
