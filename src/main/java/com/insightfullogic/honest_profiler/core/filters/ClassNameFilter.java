package com.insightfullogic.honest_profiler.core.filters;

import com.insightfullogic.honest_profiler.core.collector.Profile;
import com.insightfullogic.honest_profiler.core.parser.Method;

import java.util.HashMap;
import java.util.Map;

final class ClassNameFilter implements Filter {

    private final Map<Long, Boolean> methods;
    private final String className;

    ClassNameFilter(final String className) {
        this.className = className;
        methods = new HashMap<>();
    }

    @Override
    public void filter(Profile profile) {
        filterFlatProfile(profile);
        filterTreeProfile(profile);
    }

    private void filterTreeProfile(Profile profile) {
        // TODO
    }

    private void filterFlatProfile(Profile profile) {
        profile.getFlatProfile()
               .removeIf(entry -> !classNameMatches(entry.getMethod()));
    }

    private boolean classNameMatches(Method method) {
        return methods.computeIfAbsent(
            method.getMethodId(),
            id -> method.getClassName().contains(className));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassNameFilter that = (ClassNameFilter) o;

        if (className != null ? !className.equals(that.className) : that.className != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return className != null ? className.hashCode() : 0;
    }
}
