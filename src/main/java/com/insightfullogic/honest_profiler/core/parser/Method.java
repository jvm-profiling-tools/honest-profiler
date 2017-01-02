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
package com.insightfullogic.honest_profiler.core.parser;

import com.insightfullogic.honest_profiler.core.collector.Frame;

import java.util.Objects;

public final class Method implements LogEvent, Frame
{
    private final long methodId;
    private final String fileName;
    private final String className;
    private final String methodName;

    public Method(long methodId, String fileName, String className, String methodName)
    {
        this.methodId = methodId;
        this.fileName = fileName;
        this.className = formatClassName(className);
        this.methodName = methodName;
    }

    // Avoid formatting class name in copy()
    private Method(long methodId,
                   String fileName,
                   String className,
                   String methodName,
                   boolean dummy)
    {
        this.methodId = methodId;
        this.fileName = fileName;
        this.className = className;
        this.methodName = methodName;
    }

    private String formatClassName(String className)
    {
        if (className.isEmpty())
        {
            return className;
        }

        return className.substring(1, className.length() - 1)
            .replace('/', '.');
    }

    @Override
    public void accept(LogEventListener listener)
    {
        listener.handle(this);
    }

    @Override
    public long getMethodId()
    {
        return methodId;
    }

    public String getFileName()
    {
        return fileName;
    }

    @Override
    public String getClassName()
    {
        return className;
    }

    @Override
    public String getMethodName()
    {
        return methodName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Method method = (Method) o;
        return methodId == method.methodId;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(methodId);
    }

    @Override
    public String toString()
    {
        return "Method{" +
            "methodId=" + methodId +
            ", fileName='" + fileName + '\'' +
            ", className='" + className + '\'' +
            ", methodName='" + methodName + '\'' +
            '}';
    }

    @Override
    public int getBci()
    {
        return Frame.BCI_ERR_IGNORE;
    }

    @Override
    public int getLine()
    {
        return 0;
    }

    @Override
    public Method copy()
    {
        return new Method(methodId, fileName, className, methodName, true);
    }
}
