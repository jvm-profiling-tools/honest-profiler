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

import com.insightfullogic.honest_profiler.core.collector.FlatProfileEntry;
import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.core.profiles.ProfileNode;

abstract class TimeShareFilter implements Filter
{

    private final double minShare;

    TimeShareFilter(final double minShare)
    {
        if (minShare > 1.0)
        {
            throw new FilterParseException("Time share must be between 0.0 and 1.0, but is " + minShare);
        }
        this.minShare = minShare;
    }

    @Override
    public void filter(Profile profile)
    {
        filterFlatProfile(profile);
        filterTreeProfile(profile);
    }

    private void filterTreeProfile(Profile profile)
    {
        profile.getTrees()
            .removeIf(tree -> filterNode(tree.getRootNode()));
    }

    private boolean filterNode(ProfileNode node)
    {
        boolean dead = minShare > treeField(node);

        if (!dead)
        {
            node.getChildren()
                .removeIf(this::filterNode);
        }

        return dead;
    }

    private void filterFlatProfile(Profile profile)
    {
        profile.getFlatByMethodProfile()
            .removeIf(entry -> minShare > flatField(entry));
    }

    protected abstract double flatField(FlatProfileEntry entry);

    protected abstract double treeField(ProfileNode node);

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeShareFilter that = (TimeShareFilter) o;

        if (Double.compare(that.minShare, minShare) != 0) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        long temp = Double.doubleToLongBits(minShare);
        return (int) (temp ^ (temp >>> 32));
    }
}

final class TotalTimeShareFilter extends TimeShareFilter
{

    TotalTimeShareFilter(double minShare)
    {
        super(minShare);
    }

    @Override
    protected double flatField(FlatProfileEntry entry)
    {
        return entry.getTotalTimeShare();
    }

    @Override
    protected double treeField(ProfileNode node)
    {
        return node.getTotalTimeShare();
    }
}

final class SelfTimeShareFilter extends TimeShareFilter
{

    SelfTimeShareFilter(double minShare)
    {
        super(minShare);
    }

    @Override
    protected double flatField(FlatProfileEntry entry)
    {
        return entry.getSelfTimeShare();
    }

    @Override
    protected double treeField(ProfileNode node)
    {
        return node.getSelfTimeShare();
    }
}
