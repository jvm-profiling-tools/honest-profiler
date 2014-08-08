package com.insightfullogic.honest_profiler.core.filters;

import com.insightfullogic.honest_profiler.core.ProfileListener;
import com.insightfullogic.honest_profiler.core.collector.Profile;

import java.util.Collections;
import java.util.List;

public class ProfileFilter implements ProfileListener {

    private volatile List<Filter> filters;

    public ProfileFilter() {
        filters = Collections.emptyList();
    }

    public void updateFilters(String filterDescription) {
        filters = Filters.parse(filterDescription);
    }

    @Override
    public void accept(Profile profile) {
        filters.forEach(filter -> filter.filter(profile));
    }

}
