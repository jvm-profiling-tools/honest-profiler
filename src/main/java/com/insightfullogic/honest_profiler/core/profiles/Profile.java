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
package com.insightfullogic.honest_profiler.core.profiles;

import com.insightfullogic.honest_profiler.core.collector.FlatProfileEntry;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

/**
 * An event that represents the collected results of some profiling activity.
 */
public final class Profile
{
    private final int traceCount;
    private final List<ProfileTree> trees;
    private final List<FlatProfileEntry> flatByMethodProfile;
    private final List<FlatProfileEntry> flatByFrameProfile;

    public Profile(int traceCount,
                   List<FlatProfileEntry> flatByMethodProfile,
                   List<FlatProfileEntry> flatByFrameProfile,
                   List<ProfileTree> trees)
    {
        this.traceCount = traceCount;
        this.flatByMethodProfile = flatByMethodProfile;
        this.flatByFrameProfile = flatByFrameProfile;
        this.trees = trees;
    }

    public int getTraceCount()
    {
        return traceCount;
    }

    public List<FlatProfileEntry> getFlatByMethodProfile()
    {
        return flatByMethodProfile;
    }

    public Stream<FlatProfileEntry> flatByMethodProfile()
    {
        return flatByMethodProfile.stream();
    }

    public List<FlatProfileEntry> getFlatByFrameProfile()
    {
        return flatByFrameProfile;
    }

    public Stream<FlatProfileEntry> flatByFrameProfile()
    {
        return flatByFrameProfile.stream();
    }

    public List<ProfileTree> getTrees()
    {
        return trees;
    }

    public Profile copy()
    {
        return new Profile(
            this.traceCount,
            this.flatByMethodProfile.stream().map(FlatProfileEntry::copy).collect(toList()),
            this.flatByFrameProfile.stream().map(FlatProfileEntry::copy).collect(toList()),
            this.trees.stream().map(ProfileTree::copy).collect(toList()));
    }

    @Override
    public String toString()
    {
        return "Profile{" +
            "traceCount: " + traceCount +
            ", count(trees): " + trees +
            '}';
    }
}
