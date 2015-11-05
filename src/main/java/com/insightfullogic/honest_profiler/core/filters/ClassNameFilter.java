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

import com.insightfullogic.honest_profiler.core.collector.Frame;
import com.insightfullogic.honest_profiler.core.profiles.Profile;

import java.util.HashMap;
import java.util.Map;

final class ClassNameFilter implements Filter
{

    private final Map<Long, Boolean> methods;
    private final String className;

    ClassNameFilter(final String className)
    {
        this.className = className;
        methods = new HashMap<>();
    }

    @Override
    public void filter(Profile profile)
    {
        filterFlatProfile(profile);
    }

    private void filterFlatProfile(Profile profile)
    {
        profile.getFlatByMethodProfile()
            .removeIf(entry -> !classNameMatches(entry.getFrameInfo()));
    }

    private boolean classNameMatches(Frame sampleFrameInfo)
    {
        return methods.computeIfAbsent(
            sampleFrameInfo.getMethodId(),
            id -> sampleFrameInfo.getClassName().contains(className));
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassNameFilter that = (ClassNameFilter) o;

        if (className != null ? !className.equals(that.className) : that.className != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return className != null ? className.hashCode() : 0;
    }
}
