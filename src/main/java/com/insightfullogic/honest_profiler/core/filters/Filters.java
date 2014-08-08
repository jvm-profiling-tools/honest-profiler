package com.insightfullogic.honest_profiler.core.filters;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleFunction;
import java.util.function.Predicate;

public class Filters {

    public static final String TOTAL_TIME = "total time > ";
    public static final String SELF_TIME = "self time > ";
    public static final String CLASS_NAME = "class: ";

    private final String description;
    private final List<Filter> filters;

    private int offset;

    public static List<Filter> parse(String description) {
        return new Filters(description).parse();
    }

    private Filters(String description) {
        filters = new ArrayList<>();
        filters.add(new ThreadSampleFilter());
        offset = 0;

        if (!description.isEmpty() && description.charAt(description.length() -1) != ';') {
            description = description + ';';
        }
        this.description = description;
    }

    private List<Filter> parse() {
        while (offset < description.length()) {
            if (!(parseByPrefix(TOTAL_TIME, withNumber(TotalTimeShareFilter::new))
               || parseByPrefix(SELF_TIME, withNumber(SelfTimeShareFilter::new))
               || parseByPrefix(CLASS_NAME, this::byClassName))) {

                throw new FilterParseException("Unable to parse: " + remainingDescription());
            }
        }
        return filters;
    }

    private String remainingDescription() {
        return description.substring(offset);
    }

    private boolean byClassName(String className) {
        filters.add(new ClassNameFilter(className));
        return true;
    }

    private boolean parseByPrefix(String prefix, Predicate<String> callback) {
        if (!description.startsWith(prefix, offset)) {
            return false;
        }

        int endOfPrefix = offset + prefix.length();
        int valueIndex = description.indexOf(';', endOfPrefix);
        if (valueIndex == -1) {
            throw new FilterParseException("Unable to parse: " + remainingDescription() + " was expecting ';'");
        }
        String value = description.substring(endOfPrefix, valueIndex);
        offset = valueIndex + 1;
        return callback.test(value);
    }

    private Predicate<String> withNumber(DoubleFunction<Filter> factory) {
        return number -> {
            try {
                double minShare = Double.parseDouble(number);
                filters.add(factory.apply(minShare));
                return true;
            } catch (NumberFormatException e) {
                throw new FilterParseException(e);
            }
        };
    }

}
