package com.insightfullogic.honest_profiler.ports.javafx.model.diff;

import com.insightfullogic.honest_profiler.core.collector.FlatProfileEntry;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class FlatEntryDiff
{

    private final String className;
    private final String methodName;
    private final String fullName;

    private final IntegerProperty baseTotalCount = new SimpleIntegerProperty();
    private final IntegerProperty baseSelfCount = new SimpleIntegerProperty();
    private final IntegerProperty baseTraceCount = new SimpleIntegerProperty();

    private final IntegerProperty newTotalCount = new SimpleIntegerProperty();
    private final IntegerProperty newSelfCount = new SimpleIntegerProperty();
    private final IntegerProperty newTraceCount = new SimpleIntegerProperty();

    private final DoubleProperty baseTotalTimeShare = new SimpleDoubleProperty();
    private final DoubleProperty baseSelfTimeShare = new SimpleDoubleProperty();

    private final DoubleProperty newTotalTimeShare = new SimpleDoubleProperty();
    private final DoubleProperty newSelfTimeShare = new SimpleDoubleProperty();

    private final DoubleProperty pctTotalChange = new SimpleDoubleProperty();
    private final DoubleProperty pctSelfChange = new SimpleDoubleProperty();

    public FlatEntryDiff(FlatProfileEntry baseEntry, FlatProfileEntry newEntry)
    {
        pctTotalChange.bind(newTotalTimeShare.subtract(baseTotalTimeShare));
        pctSelfChange.bind(newSelfTimeShare.subtract(baseSelfTimeShare));

        this.className = newEntry == null ? baseEntry.getFrameInfo().getClassName()
            : newEntry.getFrameInfo().getClassName();
        this.methodName = newEntry == null ? baseEntry.getFrameInfo().getMethodName()
            : newEntry.getFrameInfo().getMethodName();
        this.fullName = newEntry == null ? baseEntry.getFrameInfo().getFullName()
            : newEntry.getFrameInfo().getFullName();

        if (baseEntry != null)
        {
            updateForBase(baseEntry);
        }

        if (newEntry != null)
        {
            updateForNew(newEntry);
        }
    }

    public String getClassName()
    {
        return className;
    }

    public String getMethodName()
    {
        return methodName;
    }

    public String getFullName()
    {
        return fullName;
    }

    public int getBaseTotalCount()
    {
        return baseTotalCount.get();
    }

    public void addBaseTotalCount(int value)
    {
        increment(baseTotalCount, value);
    }

    public int getBaseSelfCount()
    {
        return baseSelfCount.get();
    }

    public void addBaseSelfCount(int value)
    {
        increment(baseSelfCount, value);
    }

    public int getBaseTraceCount()
    {
        return baseTraceCount.get();
    }

    public int getNewTotalCount()
    {
        return newTotalCount.get();
    }

    public int getNewSelfCount()
    {
        return newSelfCount.get();
    }

    public int getNewTraceCount()
    {
        return newTraceCount.get();
    }

    public IntegerProperty baseTotalCountProperty()
    {
        return baseTotalCount;
    }

    public IntegerProperty baseSelfCountProperty()
    {
        return baseSelfCount;
    }

    public IntegerProperty baseTraceCountProperty()
    {
        return baseTraceCount;
    }

    public IntegerProperty newTotalCountProperty()
    {
        return newTotalCount;
    }

    public IntegerProperty newSelfCountProperty()
    {
        return newSelfCount;
    }

    public IntegerProperty newTraceCountProperty()
    {
        return newTraceCount;
    }

    public DoubleProperty baseTotalTimeShareProperty()
    {
        return baseTotalTimeShare;
    }

    public DoubleProperty baseSelfTimeShareProperty()
    {
        return baseSelfTimeShare;
    }

    public DoubleProperty newTotalTimeShareProperty()
    {
        return newTotalTimeShare;
    }

    public DoubleProperty newSelfTimeShareProperty()
    {
        return newSelfTimeShare;
    }

    public DoubleProperty pctSelfChangeProperty()
    {
        return pctSelfChange;
    }

    public DoubleProperty pctTotalChangeProperty()
    {
        return pctTotalChange;
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append("BS: ");
        result.append(baseSelfCount.get());
        result.append(" - BT: ");
        result.append(baseTotalCount.get());
        result.append(" - NS: ");
        result.append(newSelfCount.get());
        result.append(" - NT: ");
        result.append(newTotalCount.get());
        result.append(" - BTR: ");
        result.append(baseTraceCount.get());
        result.append(" - NTR: ");
        result.append(newTraceCount.get());
        result.append(" :: ");
        result.append(getFullName());
        return result.toString();
    }

    private final void increment(IntegerProperty property, int amount)
    {
        property.set(property.get() + amount);
    }

    private final DoubleBinding getCastBinding(IntegerProperty intProperty)
    {
        return new DoubleBinding()
        {
            {
                super.bind(intProperty);
            }

            @Override
            protected double computeValue()
            {
                return intProperty.doubleValue();
            }
        };
    }

    public FlatEntryDiff updateForBase(FlatProfileEntry entry)
    {
        baseSelfCount.set(entry.getSelfCount());
        baseTotalCount.set(entry.getTotalCount());
        baseTraceCount.set(entry.getTraceCount());

        if (entry.getTraceCount() > 0)
        {
            if (!baseTotalTimeShare.isBound())
            {
                baseTotalTimeShare.bind(baseTotalCount.divide(getCastBinding(baseTraceCount)));
                baseSelfTimeShare.bind(baseSelfCount.divide(getCastBinding(baseTraceCount)));
            }
        }

        return this;
    }

    public FlatEntryDiff updateForNew(FlatProfileEntry entry)
    {
        newSelfCount.set(entry.getSelfCount());
        newTotalCount.set(entry.getTotalCount());
        newTraceCount.set(entry.getTraceCount());

        if (entry.getTraceCount() > 0)
        {
            if (!newTotalTimeShare.isBound())
            {
                newTotalTimeShare.bind(newTotalCount.divide(getCastBinding(newTraceCount)));
                newSelfTimeShare.bind(newSelfCount.divide(getCastBinding(newTraceCount)));
            }
        }

        return this;
    }
}
