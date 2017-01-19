package com.insightfullogic.honest_profiler.core.aggregation.result;

import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.BASE_SELF_COUNT;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.BASE_SELF_COUNT_PCT;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.BASE_SELF_TIME;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.BASE_SELF_TIME_PCT;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.BASE_TOTAL_COUNT;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.BASE_TOTAL_COUNT_PCT;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.BASE_TOTAL_TIME;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.BASE_TOTAL_TIME_PCT;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.FQMN;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.NEW_SELF_COUNT;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.NEW_SELF_COUNT_PCT;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.NEW_SELF_TIME;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.NEW_SELF_TIME_PCT;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.NEW_TOTAL_COUNT;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.NEW_TOTAL_COUNT_PCT;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.NEW_TOTAL_TIME;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.NEW_TOTAL_TIME_PCT;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.SELF_COUNT;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.SELF_COUNT_DIFF;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.SELF_COUNT_PCT;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.SELF_COUNT_PCT_DIFF;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.SELF_TIME;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.SELF_TIME_DIFF;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.SELF_TIME_PCT;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.SELF_TIME_PCT_DIFF;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.TOTAL_COUNT;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.TOTAL_COUNT_DIFF;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.TOTAL_COUNT_PCT;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.TOTAL_COUNT_PCT_DIFF;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.TOTAL_TIME;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.TOTAL_TIME_DIFF;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.TOTAL_TIME_PCT;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.TOTAL_TIME_PCT_DIFF;

import java.util.Arrays;
import java.util.List;

import com.insightfullogic.honest_profiler.core.aggregation.filter.Target;

public enum ItemType
{
    ENTRY(FQMN, SELF_TIME, TOTAL_TIME, SELF_COUNT, TOTAL_COUNT, SELF_TIME_PCT, TOTAL_TIME_PCT,
        SELF_COUNT_PCT, TOTAL_COUNT_PCT),
    NODE(FQMN, SELF_TIME, TOTAL_TIME, SELF_COUNT, TOTAL_COUNT, SELF_TIME_PCT, TOTAL_TIME_PCT,
        SELF_COUNT_PCT, TOTAL_COUNT_PCT),
    DIFFENTRY(FQMN, BASE_SELF_TIME, BASE_TOTAL_TIME, BASE_SELF_COUNT, BASE_TOTAL_COUNT,
        BASE_SELF_TIME_PCT, BASE_TOTAL_TIME_PCT, BASE_SELF_COUNT_PCT, BASE_TOTAL_COUNT_PCT,
        NEW_SELF_TIME, NEW_TOTAL_TIME, NEW_SELF_COUNT, NEW_TOTAL_COUNT, NEW_SELF_TIME_PCT,
        NEW_TOTAL_TIME_PCT, NEW_SELF_COUNT_PCT, NEW_TOTAL_COUNT_PCT, SELF_TIME_DIFF,
        TOTAL_TIME_DIFF, SELF_COUNT_DIFF, TOTAL_COUNT_DIFF, SELF_TIME_PCT_DIFF, TOTAL_TIME_PCT_DIFF,
        SELF_COUNT_PCT_DIFF, TOTAL_COUNT_PCT_DIFF),
    DIFFNODE(FQMN, BASE_SELF_TIME, BASE_TOTAL_TIME, BASE_SELF_COUNT, BASE_TOTAL_COUNT,
        BASE_SELF_TIME_PCT, BASE_TOTAL_TIME_PCT, BASE_SELF_COUNT_PCT, BASE_TOTAL_COUNT_PCT,
        NEW_SELF_TIME, NEW_TOTAL_TIME, NEW_SELF_COUNT, NEW_TOTAL_COUNT, NEW_SELF_TIME_PCT,
        NEW_TOTAL_TIME_PCT, NEW_SELF_COUNT_PCT, NEW_TOTAL_COUNT_PCT, SELF_TIME_DIFF,
        TOTAL_TIME_DIFF, SELF_COUNT_DIFF, TOTAL_COUNT_DIFF, SELF_TIME_PCT_DIFF, TOTAL_TIME_PCT_DIFF,
        SELF_COUNT_PCT_DIFF, TOTAL_COUNT_PCT_DIFF),
    FLAMEGRAPH();

    private List<Target> allowedTargets;

    private ItemType(Target... allowedTargets)
    {
        this.allowedTargets = Arrays.asList(allowedTargets);
    }

    public List<Target> getAllowedTargets()
    {
        return allowedTargets;
    }
}
