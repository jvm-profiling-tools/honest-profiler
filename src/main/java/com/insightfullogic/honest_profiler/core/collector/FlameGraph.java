/**
 * Copyright (c) 2015 Richard Warburton (richard.warburton@gmail.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
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

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

/**
 * Represents the stored data model for a flamegraph
 */
public class FlameGraph
{
    private Map<List<Method>, Long> flameGraph;

    public FlameGraph(Map<List<Method>, Long> flameGraph) {
        this.flameGraph = flameGraph;
    }

    public void writeTo(Writer out) throws IOException {
        for (Map.Entry<List<Method>, Long> item : flameGraph.entrySet()) {
            writeTrace(out, item.getKey(), item.getValue());
        }
    }

    private void writeTrace(Writer out, List<Method> trace, long weight) throws IOException {
        if (trace.size() == 0)
            return;
        boolean isFirstFrame = true;

        for (int i = trace.size() - 1; i >= 0; --i) {
            Method method = trace.get(i);

            if (!isFirstFrame)
                out.write(";");
            isFirstFrame = false;

            out.write(method.getClassName());
            out.write(".");
            out.write(method.getMethodName());
        }

        out.write(" ");
        out.write(Long.toString(weight));
        out.write("\n");
    }
}
