/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
package com.insightfullogic.honest_profiler.core.filters;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleFunction;
import java.util.function.Predicate;

public class Filters
{

    private static final String TOTAL_TIME = "total time > ";
    private static final String SELF_TIME = "self time > ";
    private static final String CLASS_NAME = "class: ";

    private final String description;
    private final List<Filter> filters;

    private int offset;

    private Filters(String description)
    {
        filters = new ArrayList<>();
        filters.add(new ThreadSampleFilter());
        offset = 0;

        if (!description.isEmpty() && description.charAt(description.length() - 1) != ';')
        {
            description = description + ';';
        }
        this.description = description;
    }

    public static List<Filter> parse(String description)
    {
        return new Filters(description).parse();
    }

    private List<Filter> parse()
    {
        while (offset < description.length())
        {
            if (!(parseByPrefix(TOTAL_TIME, withNumber(TotalTimeShareFilter::new))
                || parseByPrefix(SELF_TIME, withNumber(SelfTimeShareFilter::new))
                || parseByPrefix(CLASS_NAME, this::byClassName)))
            {

                throw new FilterParseException("Unable to parse: " + remainingDescription());
            }
        }
        return filters;
    }

    private String remainingDescription()
    {
        return description.substring(offset);
    }

    private boolean byClassName(String className)
    {
        filters.add(new ClassNameFilter(className));
        return true;
    }

    private boolean parseByPrefix(String prefix, Predicate<String> callback)
    {
        if (!description.startsWith(prefix, offset))
        {
            return false;
        }

        int endOfPrefix = offset + prefix.length();
        int valueIndex = description.indexOf(';', endOfPrefix);
        if (valueIndex == -1)
        {
            throw new FilterParseException("Unable to parse: " + remainingDescription() + " was expecting ';'");
        }
        String value = description.substring(endOfPrefix, valueIndex);
        offset = valueIndex + 1;
        return callback.test(value);
    }

    private Predicate<String> withNumber(DoubleFunction<Filter> factory)
    {
        return number -> {
            try
            {
                double minShare = Double.parseDouble(number);
                filters.add(factory.apply(minShare));
                return true;
            }
            catch (NumberFormatException e)
            {
                throw new FilterParseException(e);
            }
        };
    }

}
