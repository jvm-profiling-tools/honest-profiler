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

import com.insightfullogic.honest_profiler.core.collector.Frame;

import java.util.List;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public final class ProfileNode
{
    private final List<ProfileNode> children;
    private final Frame method;
    private final int totalCount;
    private final int selfCount;
    private final int parentCount;

    public ProfileNode(Frame method, int totalCount, int parentCount)
    {
        this(method, totalCount, parentCount, emptyList());
    }

    public ProfileNode(Frame method, int totalCount, int parentCount, List<ProfileNode> children)
    {
        this.method = method;
        this.children = children;
        this.totalCount = totalCount;
        this.selfCount = totalCount - children.stream()
            .mapToInt(ProfileNode::getTotalCount)
            .sum();
        this.parentCount = parentCount;
    }

    // For efficient copy()
    private ProfileNode(Frame method,
                        int totalCount,
                        int selfCount,
                        int parentCount,
                        List<ProfileNode> children)
    {
        this.method = method;
        this.children = children;
        this.totalCount = totalCount;
        this.selfCount = selfCount;
        this.parentCount = parentCount;
    }

    public Stream<ProfileNode> children()
    {
        return children.stream();
    }

    public List<ProfileNode> getChildren()
    {
        return children;
    }

    public int getTotalCount()
    {
        return totalCount;
    }
    
    public int getSelfCount()
    {
        return selfCount;
    }
    
    public int getParentCount()
    {
        return parentCount;
    }
    
    public double getTotalTimeShare()
    {
        return totalCount / (double) parentCount;
    }

    public double getSelfTimeShare()
    {
        return selfCount / (double) parentCount;
    }

    public Frame getFrameInfo()
    {
        return method;
    }

    // Calculate deepest stack depth in descendants. Return 0 if there are no
    // children.
    public int getDescendantDepth()
    {
        if (children.isEmpty())
        {
            return 0;
        }

        int depth = 0;
        for (ProfileNode child : children)
        {
            depth = max(depth, child.getDescendantDepth() + 1);
        }
        return depth;
    }

    @Override
    public String toString()
    {
        return "PN{" + totalCount + " " + parentCount+ " " + method.getMethodName() + children + '}';
    }

    public ProfileNode copy()
    {
        return new ProfileNode(
            method.copy(),
            totalCount,
            selfCount,
            parentCount,
            children.stream().map(ProfileNode::copy).collect(toList()));
    }
}
