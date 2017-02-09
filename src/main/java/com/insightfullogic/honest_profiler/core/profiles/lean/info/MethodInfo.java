package com.insightfullogic.honest_profiler.core.profiles.lean.info;

import com.insightfullogic.honest_profiler.core.parser.Method;

/**
 * MethodInfo collects the metadata about a {@link Method}, mapping the method id to the name of the java file in which
 * the method resides, the name of the class the method is a member of, and the name of the method.
 * <p>
 * Since the method name by itself is seldom useful, the Fully Qualified Method Name (FQMN), equal to the class name +
 * "." + method name, is cached.
 */
public class MethodInfo
{
    // Instance Properties

    private final long methodId;
    private final String fileName;
    private final String className;
    private final String methodName;

    private final String cachedFqmn;

    // Instance Constructors

    /**
     * Constructor which extracts the metadata from a {@link Method} and caches the FQMN.
     * <p>
     * @param method the {@link Method} whose metadata will be stored
     */
    public MethodInfo(Method method)
    {
        methodId = method.getMethodId();
        fileName = method.getFileName();
        className = method.getClassName();
        methodName = method.getMethodName();

        StringBuilder result = new StringBuilder(className);
        result.append(".");
        result.append(methodName);
        cachedFqmn = result.toString();
    }

    // Instance Accessors

    /**
     * Returns the method id.
     * <p>
     * @return the method id
     */
    public long getMethodId()
    {
        return methodId;
    }

    /**
     * Returns the file name.
     * <p>
     * @return the file name
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * Returns the class name.
     * <p>
     * @return the class name
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * Returns the method name.
     * <p>
     * @return the method name
     */
    public String getMethodName()
    {
        return methodName;
    }

    /**
     * Returns the Fully Qualified Method Name (FQMN), which is equal to the class name + "." + method name.
     * <p>
     * @return the Fully Qualified Method Name
     */
    public String getFqmn()
    {
        return cachedFqmn;
    }

    // Object Implementation

    @Override
    public int hashCode()
    {
        return 31 + (int)(methodId ^ (methodId >>> 32));
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }

        MethodInfo other = (MethodInfo)obj;

        return (methodId == other.methodId);
    }
}
