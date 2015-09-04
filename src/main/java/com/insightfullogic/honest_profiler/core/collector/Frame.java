package com.insightfullogic.honest_profiler.core.collector;

public interface Frame {
    public static final int BCI_ERR_IGNORE = -42;
	
    long getMethodId();

    String getClassName();

    String getMethodName();
    
    int getBci();
    
    int getLine();
}
