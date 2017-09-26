package com.insightfullogic.honest_profiler.framework.scenario;

import java.util.function.Predicate;

@FunctionalInterface
public interface ScenarioDiffFilter
{
    public static ScenarioDiffFilter keyFlt(Predicate<String> condition)
    {
        return (key, baseSelfCnt, baseTotalCnt, baseSelfTime, baseTotalTime, baseSelfCntPct,
            baseTotalCntPct, baseSelfTimePct, baseTotalTimePct, newSelfCnt, newTotalCnt,
            newSelfTime, newTotalTime, newSelfCntPct, newTotalCntPct, newSelfTimePct,
            newTotalTimePct, selfCntDiff, totalCntDiff, selfTimeDiff, totalTimeDiff, selfCntPctDiff,
            totalCntPctDiff, selfTimePctDiff, totalTimePctDiff) -> condition.test(key);
    }

    public static ScenarioDiffFilter baseSelfCntFlt(Predicate<Integer> condition)
    {
        return (key, baseSelfCnt, baseTotalCnt, baseSelfTime, baseTotalTime, baseSelfCntPct,
            baseTotalCntPct, baseSelfTimePct, baseTotalTimePct, newSelfCnt, newTotalCnt,
            newSelfTime, newTotalTime, newSelfCntPct, newTotalCntPct, newSelfTimePct,
            newTotalTimePct, selfCntDiff, totalCntDiff, selfTimeDiff, totalTimeDiff, selfCntPctDiff,
            totalCntPctDiff, selfTimePctDiff, totalTimePctDiff) -> condition.test(baseSelfCnt);
    }

    public static ScenarioDiffFilter baseTotalCntFlt(Predicate<Integer> condition)
    {
        return (key, baseSelfCnt, baseTotalCnt, baseSelfTime, baseTotalTime, baseSelfCntPct,
            baseTotalCntPct, baseSelfTimePct, baseTotalTimePct, newSelfCnt, newTotalCnt,
            newSelfTime, newTotalTime, newSelfCntPct, newTotalCntPct, newSelfTimePct,
            newTotalTimePct, selfCntDiff, totalCntDiff, selfTimeDiff, totalTimeDiff, selfCntPctDiff,
            totalCntPctDiff, selfTimePctDiff, totalTimePctDiff) -> condition.test(baseTotalCnt);
    }

    public static ScenarioDiffFilter baseSelfTimeFlt(Predicate<Long> condition)
    {
        return (key, baseSelfCnt, baseTotalCnt, baseSelfTime, baseTotalTime, baseSelfCntPct,
            baseTotalCntPct, baseSelfTimePct, baseTotalTimePct, newSelfCnt, newTotalCnt,
            newSelfTime, newTotalTime, newSelfCntPct, newTotalCntPct, newSelfTimePct,
            newTotalTimePct, selfCntDiff, totalCntDiff, selfTimeDiff, totalTimeDiff, selfCntPctDiff,
            totalCntPctDiff, selfTimePctDiff, totalTimePctDiff) -> condition.test(baseSelfTime);
    }

    public static ScenarioDiffFilter baseTotalTimeFlt(Predicate<Long> condition)
    {
        return (key, baseSelfCnt, baseTotalCnt, baseSelfTime, baseTotalTime, baseSelfCntPct,
            baseTotalCntPct, baseSelfTimePct, baseTotalTimePct, newSelfCnt, newTotalCnt,
            newSelfTime, newTotalTime, newSelfCntPct, newTotalCntPct, newSelfTimePct,
            newTotalTimePct, selfCntDiff, totalCntDiff, selfTimeDiff, totalTimeDiff, selfCntPctDiff,
            totalCntPctDiff, selfTimePctDiff, totalTimePctDiff) -> condition.test(baseTotalTime);
    }

    public static ScenarioDiffFilter baseSelfCntPctFilter(Predicate<Double> condition)
    {
        return (key, baseSelfCnt, baseTotalCnt, baseSelfTime, baseTotalTime, baseSelfCntPct,
            baseTotalCntPct, baseSelfTimePct, baseTotalTimePct, newSelfCnt, newTotalCnt,
            newSelfTime, newTotalTime, newSelfCntPct, newTotalCntPct, newSelfTimePct,
            newTotalTimePct, selfCntDiff, totalCntDiff, selfTimeDiff, totalTimeDiff, selfCntPctDiff,
            totalCntPctDiff, selfTimePctDiff, totalTimePctDiff) -> condition.test(baseSelfCntPct);
    }

    public static ScenarioDiffFilter baseTotalCntPctFilter(Predicate<Double> condition)
    {
        return (key, baseSelfCnt, baseTotalCnt, baseSelfTime, baseTotalTime, baseSelfCntPct,
            baseTotalCntPct, baseSelfTimePct, baseTotalTimePct, newSelfCnt, newTotalCnt,
            newSelfTime, newTotalTime, newSelfCntPct, newTotalCntPct, newSelfTimePct,
            newTotalTimePct, selfCntDiff, totalCntDiff, selfTimeDiff, totalTimeDiff, selfCntPctDiff,
            totalCntPctDiff, selfTimePctDiff, totalTimePctDiff) -> condition.test(baseTotalCntPct);
    }

    public static ScenarioDiffFilter baseSelfTimePctFilter(Predicate<Double> condition)
    {
        return (key, baseSelfCnt, baseTotalCnt, baseSelfTime, baseTotalTime, baseSelfCntPct,
            baseTotalCntPct, baseSelfTimePct, baseTotalTimePct, newSelfCnt, newTotalCnt,
            newSelfTime, newTotalTime, newSelfCntPct, newTotalCntPct, newSelfTimePct,
            newTotalTimePct, selfCntDiff, totalCntDiff, selfTimeDiff, totalTimeDiff, selfCntPctDiff,
            totalCntPctDiff, selfTimePctDiff, totalTimePctDiff) -> condition.test(baseSelfTimePct);
    }

    public static ScenarioDiffFilter baseTotalTimePctFilter(Predicate<Double> condition)
    {
        return (key, baseSelfCnt, baseTotalCnt, baseSelfTime, baseTotalTime, baseSelfCntPct,
            baseTotalCntPct, baseSelfTimePct, baseTotalTimePct, newSelfCnt, newTotalCnt,
            newSelfTime, newTotalTime, newSelfCntPct, newTotalCntPct, newSelfTimePct,
            newTotalTimePct, selfCntDiff, totalCntDiff, selfTimeDiff, totalTimeDiff, selfCntPctDiff,
            totalCntPctDiff, selfTimePctDiff, totalTimePctDiff) -> condition.test(baseTotalTimePct);
    }

    public static ScenarioDiffFilter newSelfCntFlt(Predicate<Integer> condition)
    {
        return (key, baseSelfCnt, baseTotalCnt, baseSelfTime, baseTotalTime, baseSelfCntPct,
            baseTotalCntPct, baseSelfTimePct, baseTotalTimePct, newSelfCnt, newTotalCnt,
            newSelfTime, newTotalTime, newSelfCntPct, newTotalCntPct, newSelfTimePct,
            newTotalTimePct, selfCntDiff, totalCntDiff, selfTimeDiff, totalTimeDiff, selfCntPctDiff,
            totalCntPctDiff, selfTimePctDiff, totalTimePctDiff) -> condition.test(newSelfCnt);
    }

    public static ScenarioDiffFilter newTotalCntFlt(Predicate<Integer> condition)
    {
        return (key, baseSelfCnt, baseTotalCnt, baseSelfTime, baseTotalTime, baseSelfCntPct,
            baseTotalCntPct, baseSelfTimePct, baseTotalTimePct, newSelfCnt, newTotalCnt,
            newSelfTime, newTotalTime, newSelfCntPct, newTotalCntPct, newSelfTimePct,
            newTotalTimePct, selfCntDiff, totalCntDiff, selfTimeDiff, totalTimeDiff, selfCntPctDiff,
            totalCntPctDiff, selfTimePctDiff, totalTimePctDiff) -> condition.test(newTotalCnt);
    }

    public static ScenarioDiffFilter newSelfTimeFlt(Predicate<Long> condition)
    {
        return (key, baseSelfCnt, baseTotalCnt, baseSelfTime, baseTotalTime, baseSelfCntPct,
            baseTotalCntPct, baseSelfTimePct, baseTotalTimePct, newSelfCnt, newTotalCnt,
            newSelfTime, newTotalTime, newSelfCntPct, newTotalCntPct, newSelfTimePct,
            newTotalTimePct, selfCntDiff, totalCntDiff, selfTimeDiff, totalTimeDiff, selfCntPctDiff,
            totalCntPctDiff, selfTimePctDiff, totalTimePctDiff) -> condition.test(newSelfTime);
    }

    public static ScenarioDiffFilter newTotalTimeFlt(Predicate<Long> condition)
    {
        return (key, baseSelfCnt, baseTotalCnt, baseSelfTime, baseTotalTime, baseSelfCntPct,
            baseTotalCntPct, baseSelfTimePct, baseTotalTimePct, newSelfCnt, newTotalCnt,
            newSelfTime, newTotalTime, newSelfCntPct, newTotalCntPct, newSelfTimePct,
            newTotalTimePct, selfCntDiff, totalCntDiff, selfTimeDiff, totalTimeDiff, selfCntPctDiff,
            totalCntPctDiff, selfTimePctDiff, totalTimePctDiff) -> condition.test(newTotalTime);
    }

    public static ScenarioDiffFilter newSelfCntPctFilter(Predicate<Double> condition)
    {
        return (key, baseSelfCnt, baseTotalCnt, baseSelfTime, baseTotalTime, baseSelfCntPct,
            baseTotalCntPct, baseSelfTimePct, baseTotalTimePct, newSelfCnt, newTotalCnt,
            newSelfTime, newTotalTime, newSelfCntPct, newTotalCntPct, newSelfTimePct,
            newTotalTimePct, selfCntDiff, totalCntDiff, selfTimeDiff, totalTimeDiff, selfCntPctDiff,
            totalCntPctDiff, selfTimePctDiff, totalTimePctDiff) -> condition.test(newSelfCntPct);
    }

    public static ScenarioDiffFilter newTotalCntPctFilter(Predicate<Double> condition)
    {
        return (key, baseSelfCnt, baseTotalCnt, baseSelfTime, baseTotalTime, baseSelfCntPct,
            baseTotalCntPct, baseSelfTimePct, baseTotalTimePct, newSelfCnt, newTotalCnt,
            newSelfTime, newTotalTime, newSelfCntPct, newTotalCntPct, newSelfTimePct,
            newTotalTimePct, selfCntDiff, totalCntDiff, selfTimeDiff, totalTimeDiff, selfCntPctDiff,
            totalCntPctDiff, selfTimePctDiff, totalTimePctDiff) -> condition.test(newTotalCntPct);
    }

    public static ScenarioDiffFilter newSelfTimePctFilter(Predicate<Double> condition)
    {
        return (key, baseSelfCnt, baseTotalCnt, baseSelfTime, baseTotalTime, baseSelfCntPct,
            baseTotalCntPct, baseSelfTimePct, baseTotalTimePct, newSelfCnt, newTotalCnt,
            newSelfTime, newTotalTime, newSelfCntPct, newTotalCntPct, newSelfTimePct,
            newTotalTimePct, selfCntDiff, totalCntDiff, selfTimeDiff, totalTimeDiff, selfCntPctDiff,
            totalCntPctDiff, selfTimePctDiff, totalTimePctDiff) -> condition.test(newSelfTimePct);
    }

    public static ScenarioDiffFilter newTotalTimePctFilter(Predicate<Double> condition)
    {
        return (key, baseSelfCnt, baseTotalCnt, baseSelfTime, baseTotalTime, baseSelfCntPct,
            baseTotalCntPct, baseSelfTimePct, baseTotalTimePct, newSelfCnt, newTotalCnt,
            newSelfTime, newTotalTime, newSelfCntPct, newTotalCntPct, newSelfTimePct,
            newTotalTimePct, selfCntDiff, totalCntDiff, selfTimeDiff, totalTimeDiff, selfCntPctDiff,
            totalCntPctDiff, selfTimePctDiff, totalTimePctDiff) -> condition.test(newTotalTimePct);
    }

    /////////////////

    public static ScenarioDiffFilter selfCntDiffFlt(Predicate<Integer> condition)
    {
        return (key, baseSelfCnt, baseTotalCnt, baseSelfTime, baseTotalTime, baseSelfCntPct,
            baseTotalCntPct, baseSelfTimePct, baseTotalTimePct, newSelfCnt, newTotalCnt,
            newSelfTime, newTotalTime, newSelfCntPct, newTotalCntPct, newSelfTimePct,
            newTotalTimePct, selfCntDiff, totalCntDiff, selfTimeDiff, totalTimeDiff, selfCntPctDiff,
            totalCntPctDiff, selfTimePctDiff, totalTimePctDiff) -> condition.test(selfCntDiff);
    }

    public static ScenarioDiffFilter totalCntDiffFlt(Predicate<Integer> condition)
    {
        return (key, baseSelfCnt, baseTotalCnt, baseSelfTime, baseTotalTime, baseSelfCntPct,
            baseTotalCntPct, baseSelfTimePct, baseTotalTimePct, newSelfCnt, newTotalCnt,
            newSelfTime, newTotalTime, newSelfCntPct, newTotalCntPct, newSelfTimePct,
            newTotalTimePct, selfCntDiff, totalCntDiff, selfTimeDiff, totalTimeDiff, selfCntPctDiff,
            totalCntPctDiff, selfTimePctDiff, totalTimePctDiff) -> condition.test(totalCntDiff);
    }

    public static ScenarioDiffFilter selfTimeDiffFlt(Predicate<Long> condition)
    {
        return (key, baseSelfCnt, baseTotalCnt, baseSelfTime, baseTotalTime, baseSelfCntPct,
            baseTotalCntPct, baseSelfTimePct, baseTotalTimePct, newSelfCnt, newTotalCnt,
            newSelfTime, newTotalTime, newSelfCntPct, newTotalCntPct, newSelfTimePct,
            newTotalTimePct, selfCntDiff, totalCntDiff, selfTimeDiff, totalTimeDiff, selfCntPctDiff,
            totalCntPctDiff, selfTimePctDiff, totalTimePctDiff) -> condition.test(selfTimeDiff);
    }

    public static ScenarioDiffFilter totalTimeDiffFlt(Predicate<Long> condition)
    {
        return (key, baseSelfCnt, baseTotalCnt, baseSelfTime, baseTotalTime, baseSelfCntPct,
            baseTotalCntPct, baseSelfTimePct, baseTotalTimePct, newSelfCnt, newTotalCnt,
            newSelfTime, newTotalTime, newSelfCntPct, newTotalCntPct, newSelfTimePct,
            newTotalTimePct, selfCntDiff, totalCntDiff, selfTimeDiff, totalTimeDiff, selfCntPctDiff,
            totalCntPctDiff, selfTimePctDiff, totalTimePctDiff) -> condition.test(totalTimeDiff);
    }

    public static ScenarioDiffFilter selfCntPctDiffFilter(Predicate<Double> condition)
    {
        return (key, baseSelfCnt, baseTotalCnt, baseSelfTime, baseTotalTime, baseSelfCntPct,
            baseTotalCntPct, baseSelfTimePct, baseTotalTimePct, newSelfCnt, newTotalCnt,
            newSelfTime, newTotalTime, newSelfCntPct, newTotalCntPct, newSelfTimePct,
            newTotalTimePct, selfCntDiff, totalCntDiff, selfTimeDiff, totalTimeDiff, selfCntPctDiff,
            totalCntPctDiff, selfTimePctDiff, totalTimePctDiff) -> condition.test(selfCntPctDiff);
    }

    public static ScenarioDiffFilter totalCntPctDiffFilter(Predicate<Double> condition)
    {
        return (key, baseSelfCnt, baseTotalCnt, baseSelfTime, baseTotalTime, baseSelfCntPct,
            baseTotalCntPct, baseSelfTimePct, baseTotalTimePct, newSelfCnt, newTotalCnt,
            newSelfTime, newTotalTime, newSelfCntPct, newTotalCntPct, newSelfTimePct,
            newTotalTimePct, selfCntDiff, totalCntDiff, selfTimeDiff, totalTimeDiff, selfCntPctDiff,
            totalCntPctDiff, selfTimePctDiff, totalTimePctDiff) -> condition.test(totalCntPctDiff);
    }

    public static ScenarioDiffFilter selfTimePctDiffFilter(Predicate<Double> condition)
    {
        return (key, baseSelfCnt, baseTotalCnt, baseSelfTime, baseTotalTime, baseSelfCntPct,
            baseTotalCntPct, baseSelfTimePct, baseTotalTimePct, newSelfCnt, newTotalCnt,
            newSelfTime, newTotalTime, newSelfCntPct, newTotalCntPct, newSelfTimePct,
            newTotalTimePct, selfCntDiff, totalCntDiff, selfTimeDiff, totalTimeDiff, selfCntPctDiff,
            totalCntPctDiff, selfTimePctDiff, totalTimePctDiff) -> condition.test(selfTimePctDiff);
    }

    public static ScenarioDiffFilter totalTimePctDiffFilter(Predicate<Double> condition)
    {
        return (key, baseSelfCnt, baseTotalCnt, baseSelfTime, baseTotalTime, baseSelfCntPct,
            baseTotalCntPct, baseSelfTimePct, baseTotalTimePct, newSelfCnt, newTotalCnt,
            newSelfTime, newTotalTime, newSelfCntPct, newTotalCntPct, newSelfTimePct,
            newTotalTimePct, selfCntDiff, totalCntDiff, selfTimeDiff, totalTimeDiff, selfCntPctDiff,
            totalCntPctDiff, selfTimePctDiff, totalTimePctDiff) -> condition.test(totalTimePctDiff);
    }

    boolean accept(String key, int baseSelfCnt, int baseTotalCnt, long baseSelfTime,
        long baseTotalTime, double baseSelfCntPct, double baseTotalCntPct, double baseSelfTimePct,
        double baseTotalTimePct, int newSelfCnt, int newTotalCnt, long newSelfTime,
        long newTotalTime, double newSelfCntPct, double newTotalCntPct, double newSelfTimePct,
        double newTotalTimePct, int selfCntDiff, int totalCntDiff, long selfTimeDiff,
        long totalTimeDiff, double selfCntPctDiff, double totalCntPctDiff, double selfTimePctDiff,
        double totalTimePctDiff);
}
