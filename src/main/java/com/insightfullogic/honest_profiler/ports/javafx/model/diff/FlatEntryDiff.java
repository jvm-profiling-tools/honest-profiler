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

    private final IntegerProperty baseTotalCnt = new SimpleIntegerProperty();
    private final IntegerProperty baseSelfCnt = new SimpleIntegerProperty();
    private final IntegerProperty baseProfileCnt = new SimpleIntegerProperty();
    private final DoubleProperty baseTotalPct = new SimpleDoubleProperty();
    private final DoubleProperty baseSelfPct = new SimpleDoubleProperty();

    private final IntegerProperty newTotalCnt = new SimpleIntegerProperty();
    private final IntegerProperty newSelfCnt = new SimpleIntegerProperty();
    private final IntegerProperty newProfileCnt = new SimpleIntegerProperty();
    private final DoubleProperty newTotalPct = new SimpleDoubleProperty();
    private final DoubleProperty newSelfPct = new SimpleDoubleProperty();

    private final IntegerProperty totalCntDiff = new SimpleIntegerProperty();
    private final IntegerProperty selfCntDiff = new SimpleIntegerProperty();
    private final IntegerProperty profileCntDiff = new SimpleIntegerProperty();
    private final DoubleProperty totalPctDiff = new SimpleDoubleProperty();
    private final DoubleProperty selfPctDiff = new SimpleDoubleProperty();

    public FlatEntryDiff(FlatProfileEntry baseEntry, FlatProfileEntry newEntry)
    {
        totalCntDiff.bind(newTotalCnt.subtract(baseTotalCnt));
        selfCntDiff.bind(newSelfCnt.subtract(baseSelfCnt));
        profileCntDiff.bind(newProfileCnt.subtract(baseProfileCnt));
        totalPctDiff.bind(newTotalPct.subtract(baseTotalPct));
        selfPctDiff.bind(newSelfPct.subtract(baseSelfPct));

        className = newEntry == null ? baseEntry.getFrameInfo().getClassName()
            : newEntry.getFrameInfo().getClassName();
        methodName = newEntry == null ? baseEntry.getFrameInfo().getMethodName()
            : newEntry.getFrameInfo().getMethodName();
        fullName = newEntry == null ? baseEntry.getFrameInfo().getFullName()
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

    public int getBaseTotalCnt()
    {
        return baseTotalCnt.get();
    }

    public int getBaseSelfCnt()
    {
        return baseSelfCnt.get();
    }

    public int getBaseProfileCnt()
    {
        return baseProfileCnt.get();
    }

    public double getBaseTotalPct()
    {
        return baseTotalPct.get();
    }

    public double getBaseSelfPct()
    {
        return baseSelfPct.get();
    }

    public int getNewTotalCnt()
    {
        return newTotalCnt.get();
    }

    public int getNewSelfCnt()
    {
        return newSelfCnt.get();
    }

    public int getNewProfileCnt()
    {
        return newProfileCnt.get();
    }

    public double getNewTotalPct()
    {
        return newTotalPct.get();
    }

    public double getNewSelfPct()
    {
        return newSelfPct.get();
    }

    public int getTotalCntDiff()
    {
        return totalCntDiff.get();
    }

    public int getSelfCntDiff()
    {
        return selfCntDiff.get();
    }

    public int getProfileCntDiff()
    {
        return profileCntDiff.get();
    }

    public double getTotalPctDiff()
    {
        return totalPctDiff.get();
    }

    public double getSelfPctDiff()
    {
        return selfPctDiff.get();
    }

    public void addBaseTotalCnt(int value)
    {
        increment(baseTotalCnt, value);
    }

    public void addBaseSelfCnt(int value)
    {
        increment(baseSelfCnt, value);
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append("BS: ");
        result.append(baseSelfCnt.get());
        result.append(" - BT: ");
        result.append(baseTotalCnt.get());
        result.append(" - NS: ");
        result.append(newSelfCnt.get());
        result.append(" - NT: ");
        result.append(newTotalCnt.get());
        result.append(" - BTR: ");
        result.append(baseProfileCnt.get());
        result.append(" - NTR: ");
        result.append(newProfileCnt.get());
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
        baseSelfCnt.set(baseSelfCnt.get() + entry.getSelfCount());
        baseTotalCnt.set(baseTotalCnt.get() + entry.getTotalCount());
        baseProfileCnt.set(entry.getTraceCount());

        if (entry.getTraceCount() > 0)
        {
            if (!baseTotalPct.isBound())
            {
                baseTotalPct.bind(baseTotalCnt.divide(getCastBinding(baseProfileCnt)));
                baseSelfPct.bind(baseSelfCnt.divide(getCastBinding(baseProfileCnt)));
            }
        }

        return this;
    }

    public FlatEntryDiff updateForNew(FlatProfileEntry entry)
    {
        newSelfCnt.set(newSelfCnt.get() + entry.getSelfCount());
        newTotalCnt.set(newSelfCnt.get() + entry.getTotalCount());
        newProfileCnt.set(entry.getTraceCount());

        if (entry.getTraceCount() > 0)
        {
            if (!newTotalPct.isBound())
            {
                newTotalPct.bind(newTotalCnt.divide(getCastBinding(newProfileCnt)));
                newSelfPct.bind(newSelfCnt.divide(getCastBinding(newProfileCnt)));
            }
        }

        return this;
    }
}
