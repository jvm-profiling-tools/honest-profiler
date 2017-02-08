package com.insightfullogic.honest_profiler.core.profiles.lean.info;

import com.insightfullogic.honest_profiler.core.parser.ThreadMeta;

/**
 * ThreadInfo collects the metadata about a thread. It records the id and, if available, the name.
 *
 * It is not immutable, because the thread name is extracted from {@link ThreadMeta} events emitted by the profiling
 * agent. Sometimes multiple {@link ThreadMeta}s are emitted for the same thread, and only one (from observation, most
 * often the first) actually contains the name. The ThreadInfo therefore contains update logic reflecting this.
 */
public class ThreadInfo
{
    // Instance Properties

    private final long id;
    private String name;

    // Instance Constructors

    /**
     * Constructor which extracts the metadata from a {@link ThreadMeta}.
     *
     * @param meta the {@link ThreadMeta} whose metadata will be stored
     */
    public ThreadInfo(ThreadMeta meta)
    {
        id = meta.getThreadId();
        name = meta.getThreadName();
    }

    // Instance Accessors

    /**
     * Returns the thread id.
     *
     * @return the thread id
     */
    public long getId()
    {
        return id;
    }

    /**
     * Returns the thread name.
     *
     * @return the thread name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the thread name, if it is not null or empty.
     *
     * @param name the (new) thread name
     * @return this object
     */
    public ThreadInfo checkAndSetName(String name)
    {
        if (name != null && !name.isEmpty())
        {
            this.name = name;
        }
        return this;
    }

    /**
     * Constructs and returns a String used for displaying the identifying information of the thread. The format is :
     * <code>  (&lt;name&gt; || "Unknown") &lt;id&gt;</code>
     *
     * @return the constructed display name
     */
    public String getIdentification()
    {
        StringBuilder result = new StringBuilder();
        result.append(name == null || name.isEmpty() ? "Unknown" : name);
        result.append(" <");
        result.append(id);
        result.append(">");
        return result.toString();
    }

    // Object Implementation

    @Override
    public int hashCode()
    {
        return 31 + (int)(id ^ (id >>> 32));
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

        ThreadInfo other = (ThreadInfo)obj;
        return (id == other.id);
    }
}
