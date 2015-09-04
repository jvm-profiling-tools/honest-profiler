package com.insightfullogic.honest_profiler.core.collector;

import com.insightfullogic.honest_profiler.core.parser.Method;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;

public class FullFrameInfo implements Frame {
    final Method method;
    final StackFrame frame;

    public FullFrameInfo(Method method, StackFrame frame) {
        if(method == null || frame == null) {
            throw new NullPointerException();
        }
        if(method.getMethodId() != frame.getMethodId()) {
            throw new IllegalArgumentException();
        }
        this.method = method;
        this.frame = frame;
    }

    @Override
    public long getMethodId() {
        return method.getMethodId();
    }

    @Override
    public String getClassName() {
        return method.getClassName();
    }

    @Override
    public String getMethodName() {
        return method.getMethodName();
    }

    @Override
    public int getBci() {
        return frame.getBci();
    }

    @Override
    public int getLine() {
        return frame.getLineNumber();
    }

    @Override
    public int hashCode() {
        return frame.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof FullFrameInfo) {
            return this.frame.equals(((FullFrameInfo)obj).frame);
        }
        return false;
    }
}
