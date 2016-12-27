package com.insightfullogic.honest_profiler.ports.javafx.model.diff;

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

    private final IntegerProperty baseTotalCount;
    private final IntegerProperty baseSelfCount;
    private final IntegerProperty baseTraceCount = new SimpleIntegerProperty();

    private final IntegerProperty newTotalCount;
    private final IntegerProperty newSelfCount;
    private final IntegerProperty newTraceCount = new SimpleIntegerProperty();

    private final DoubleProperty baseTotalTimeShare = new SimpleDoubleProperty(0);
    private final DoubleProperty baseSelfTimeShare = new SimpleDoubleProperty(0);

    private final DoubleProperty newTotalTimeShare = new SimpleDoubleProperty(0);
    private final DoubleProperty newSelfTimeShare = new SimpleDoubleProperty(0);

    private final DoubleProperty pctTotalChange = new SimpleDoubleProperty(0);
    private final DoubleProperty pctSelfChange = new SimpleDoubleProperty(0);

    public FlatEntryDiff(String className,
                         String methodName,
                         int baseTotalCount,
                         int baseSelfCount,
                         int baseTraceCount,
                         int newTotalCount,
                         int newSelfCount,
                         int newTraceCount)
    {
        super();

        this.className = className;
        this.methodName = methodName;
        fullName = new StringBuilder(this.className).append(".").append(this.methodName)
            .toString();

        this.baseTotalCount = new SimpleIntegerProperty(baseTotalCount);
        this.baseSelfCount = new SimpleIntegerProperty(baseSelfCount);
        this.newTotalCount = new SimpleIntegerProperty(newTotalCount);
        this.newSelfCount = new SimpleIntegerProperty(newSelfCount);

        setBaseTraceCount(baseTraceCount);
        setNewTraceCount(newTraceCount);

        pctTotalChange.bind(newTotalTimeShare.subtract(baseTotalTimeShare));
        pctSelfChange.bind(newSelfTimeShare.subtract(baseSelfTimeShare));
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

    public void setBaseTraceCount(int value)
    {
        if (value > 0)
        {
            baseTraceCount.set(value);

            if (!baseTotalTimeShare.isBound())
            {
                baseTotalTimeShare.bind(baseTotalCount.divide(getCastBinding(baseTraceCount)));
                baseSelfTimeShare.bind(baseSelfCount.divide(getCastBinding(baseTraceCount)));
            }
        }
    }

    public int getNewTotalCount()
    {
        return newTotalCount.get();
    }

    public void addNewTotalCount(int value)
    {
        increment(newTotalCount, value);
    }

    public int getNewSelfCount()
    {
        return newSelfCount.get();
    }

    public void addNewSelfCount(int value)
    {
        increment(newSelfCount, value);
    }

    public int getNewTraceCount()
    {
        return newTraceCount.get();
    }

    public void setNewTraceCount(int value)
    {
        if (value > 0)
        {
            newTraceCount.set(value);

            if (!newTotalTimeShare.isBound())
            {
                newTotalTimeShare.bind(newTotalCount.divide(getCastBinding(newTraceCount)));
                newSelfTimeShare.bind(newSelfCount.divide(getCastBinding(newTraceCount)));
            }
        }
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
}
