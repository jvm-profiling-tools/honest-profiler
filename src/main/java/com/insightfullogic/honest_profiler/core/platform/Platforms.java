package com.insightfullogic.honest_profiler.core.platform;


public class Platforms
{
    private Platforms() {}

    public static String getDynamicLibraryExtension()
    {
        if (isOsx())
        {
            return ".dylib";
        }
        // Default is .so
        return ".so";
    }

    private static boolean isOsx()
    {
        return getOsName().toUpperCase().contains("MAC");
    }

    private static String getOsName()
    {
        return System.getProperty("os.name");
    }
}
