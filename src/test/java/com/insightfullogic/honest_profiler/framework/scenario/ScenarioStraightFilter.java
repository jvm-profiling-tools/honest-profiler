package com.insightfullogic.honest_profiler.framework.scenario;

import java.util.function.Predicate;

@FunctionalInterface
public interface ScenarioStraightFilter
{
    public static ScenarioStraightFilter keyFlt(Predicate<String> condition)
    {
        return (key, selfCnt, totalCnt, selfTime, totalTime, selfCntPct, totalCntPct, selfTimePct,
            totalTimePct) -> condition.test(key);
    }

    public static ScenarioStraightFilter selfCntFlt(Predicate<Integer> condition)
    {
        return (key, selfCnt, totalCnt, selfTime, totalTime, selfCntPct, totalCntPct, selfTimePct,
            totalTimePct) -> condition.test(selfCnt);
    }

    public static ScenarioStraightFilter totalCntFlt(Predicate<Integer> condition)
    {
        return (key, selfCnt, totalCnt, selfTime, totalTime, selfCntPct, totalCntPct, selfTimePct,
            totalTimePct) -> condition.test(totalCnt);
    }

    public static ScenarioStraightFilter selfTimeFlt(Predicate<Long> condition)
    {
        return (key, selfCnt, totalCnt, selfTime, totalTime, selfCntPct, totalCntPct, selfTimePct,
            totalTimePct) -> condition.test(selfTime);
    }

    public static ScenarioStraightFilter totalTimeFlt(Predicate<Long> condition)
    {
        return (key, selfCnt, totalCnt, selfTime, totalTime, selfCntPct, totalCntPct, selfTimePct,
            totalTimePct) -> condition.test(totalTime);
    }

    public static ScenarioStraightFilter selfCntPctFilter(Predicate<Double> condition)
    {
        return (key, selfCnt, totalCnt, selfTime, totalTime, selfCntPct, totalCntPct, selfTimePct,
            totalTimePct) -> condition.test(selfCntPct);
    }

    public static ScenarioStraightFilter totalCntPctFilter(Predicate<Double> condition)
    {
        return (key, selfCnt, totalCnt, selfTime, totalTime, selfCntPct, totalCntPct, selfTimePct,
            totalTimePct) -> condition.test(totalCntPct);
    }

    public static ScenarioStraightFilter selfTimePctFilter(Predicate<Double> condition)
    {
        return (key, selfCnt, totalCnt, selfTime, totalTime, selfCntPct, totalCntPct, selfTimePct,
            totalTimePct) -> condition.test(selfTimePct);
    }

    public static ScenarioStraightFilter totalTimePctFilter(Predicate<Double> condition)
    {
        return (key, selfCnt, totalCnt, selfTime, totalTime, selfCntPct, totalCntPct, selfTimePct,
            totalTimePct) -> condition.test(totalTimePct);
    }

    boolean accept(String key, int selfCnt, int totalCnt, long selfTime, long totalTime,
        double selfCntPct, double totalCntPct, double selfTimePct, double totalTimePct);
}
