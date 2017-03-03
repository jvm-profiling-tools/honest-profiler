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

import com.insightfullogic.honest_profiler.core.parser.ThreadMeta;

public final class ProfileTree
{
    private final int numberOfSamples;
    private final ThreadMeta threadMeta;
    private final ProfileNode rootNode;

    public ProfileTree(long threadId, ProfileNode rootNode, int numberOfSamples)
    {
        this(new ThreadMeta(threadId, ""), rootNode, numberOfSamples);
    }

    public ProfileTree(ThreadMeta threadMeta, ProfileNode rootNode, int numberOfSamples)
    {
        this.threadMeta = threadMeta;
        this.rootNode = rootNode;
        this.numberOfSamples = numberOfSamples;
    }

    public int getNumberOfSamples()
    {
        return numberOfSamples;
    }

    public ProfileNode getRootNode()
    {
        return rootNode;
    }

    public long getThreadId()
    {
        return threadMeta.getThreadId();
    }

    public String getThreadName()
    {
        return threadMeta.getThreadName();
    }

    @Override
    public String toString()
    {
        return "ProfileTree{" +
            rootNode +
            '}';
    }

    public ProfileTree copy()
    {
        return new ProfileTree(threadMeta.copy(), rootNode.copy(), numberOfSamples);
    }
}
