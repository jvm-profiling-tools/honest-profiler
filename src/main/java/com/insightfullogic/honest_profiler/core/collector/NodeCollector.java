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
package com.insightfullogic.honest_profiler.core.collector;

import com.insightfullogic.honest_profiler.core.parser.Method;
import com.insightfullogic.honest_profiler.core.profiles.ProfileNode;

import java.util.*;
import java.util.function.LongFunction;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public final class NodeCollector
{

    private static final Comparator<ProfileNode> bySelfTimeShare =
        comparing(ProfileNode::getTotalTimeShare).reversed();

    private final Map<Long, NodeCollector> childrenByMethodId;

    private long methodId;

    private int visits;

    NodeCollector(long methodId)
    {
        this(methodId, 1);
    }

    private NodeCollector(long methodId, int visits)
    {
        this.methodId = methodId;
        this.visits = visits;
        childrenByMethodId = new HashMap<>();
    }

    public Stream<NodeCollector> children()
    {
        return childrenByMethodId.values().stream();
    }

    public List<NodeCollector> getChildren()
    {
        return new ArrayList<>(childrenByMethodId.values());
    }

    NodeCollector newChildCall(long methodId)
    {
        return childrenByMethodId.compute(methodId, (id, prev) ->
                prev == null ? new NodeCollector(id)
                    : prev.callAgain()
        );
    }

    NodeCollector callAgain()
    {
        visits++;
        return this;
    }

    /*
     * Only gets called on a root node.
     */
    ProfileNode normalise(LongFunction<Method> nameRegistry)
    {
        return normaliseBy(visits, nameRegistry);
    }

    private ProfileNode normaliseBy(int parentVisits, LongFunction<Method> nameRegistry)
    {
        Method method = nameRegistry.apply(methodId);

        List<ProfileNode> children
            = childrenByMethodId.values()
            .stream()
            .map(child -> child.normaliseBy(parentVisits, nameRegistry))
            .sorted(bySelfTimeShare)
            .collect(toList());

        return new ProfileNode(method, visits, parentVisits, children);
    }


    int getNumberOfVisits()
    {
        return visits;
    }

}
