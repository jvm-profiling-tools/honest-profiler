package com.insightfullogic.honest_profiler.ports.javafx.model.diff;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.collector.FlatProfileEntry;

import javafx.collections.ObservableList;

public class FlatProfileDiff
{
    private Map<String, FlatEntryDiff> entriesByMethodName;
    private ObservableList<FlatEntryDiff> entries;

    public FlatProfileDiff(ObservableList<FlatEntryDiff> entries)
    {
        entriesByMethodName = new HashMap<>();
        this.entries = entries;
    }

    public ObservableList<FlatEntryDiff> getEntries()
    {
        return entries;
    }

    public void clear()
    {
        entries.clear();
        entriesByMethodName.clear();
    }

    public void updateBase(List<FlatProfileEntry> baseProfile)
    {
        baseProfile.forEach(
            entry -> entriesByMethodName.compute(
                entry.getFrameInfo().getFullName(),
                (methodName, diff) -> diff == null
                    ? newDiff(entry, true) : diff.updateForBase(entry)));
    }

    public void updateNew(List<FlatProfileEntry> newProfile)
    {
        newProfile.forEach(
            entry -> entriesByMethodName.compute(
                entry.getFrameInfo().getFullName(),
                (methodName, diff) -> diff == null
                    ? newDiff(entry, false) : diff.updateForNew(entry)));
    }

    private FlatEntryDiff newDiff(FlatProfileEntry entry, boolean base)
    {
        FlatEntryDiff diff = base ? new FlatEntryDiff(entry, null)
            : new FlatEntryDiff(null, entry);
        entries.add(diff);
        return diff;
    }
}
