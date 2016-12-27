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
                entry.getFrameInfo().getClassName() + "." + entry.getFrameInfo().getMethodName(),

                (String methodName, FlatEntryDiff flatEntryDiff) ->
                {
                    if (flatEntryDiff == null)
                    {
                        FlatEntryDiff newEntry = new FlatEntryDiff(
                            entry.getFrameInfo().getClassName(),
                            entry.getFrameInfo().getMethodName(),
                            entry.getTotalCount(),
                            entry.getSelfCount(),
                            entry.getTraceCount(),
                            0,
                            0,
                            0);
                        entries.add(newEntry);
                        return newEntry;
                    }
                    else
                    {
                        flatEntryDiff.addBaseTotalCount(entry.getTotalCount());
                        flatEntryDiff.addBaseSelfCount(entry.getSelfCount());

                        if (flatEntryDiff.getBaseTraceCount() == 0)
                        {
                            flatEntryDiff.setBaseTraceCount(entry.getTraceCount());
                        }
                        return flatEntryDiff;
                    }
                }));
    }

    public void updateNew(List<FlatProfileEntry> newProfile)
    {
        newProfile.forEach(
            entry -> entriesByMethodName.compute(
                entry.getFrameInfo().getClassName() + "." + entry.getFrameInfo().getMethodName(),

                (String methodName, FlatEntryDiff flatEntryDiff) ->
                {
                    if (flatEntryDiff == null)
                    {
                        FlatEntryDiff newEntry = new FlatEntryDiff(
                            entry.getFrameInfo().getClassName(),
                            entry.getFrameInfo().getMethodName(),
                            0,
                            0,
                            0,
                            entry.getTotalCount(),
                            entry.getSelfCount(),
                            entry.getTraceCount());
                        entries.add(newEntry);
                        return newEntry;
                    }
                    else
                    {
                        flatEntryDiff.addNewTotalCount(entry.getTotalCount());
                        flatEntryDiff.addNewSelfCount(entry.getSelfCount());

                        if (flatEntryDiff.getNewTraceCount() == 0)
                        {
                            flatEntryDiff.setNewTraceCount(entry.getTraceCount());
                        }
                        return flatEntryDiff;
                    }
                }));
    }
}
