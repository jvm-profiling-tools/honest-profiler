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
package com.insightfullogic.honest_profiler.core.sources;

import com.insightfullogic.honest_profiler.ports.sources.FileLogSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Represents a Java Virtual Machine
 */
public class VirtualMachine implements Comparable<VirtualMachine>
{
    private static final String OPT_AGENT = "-agentpath";
    private static final String OPT_LOG = "logPath=";
    
    private final String id;
    private final String displayName;
    private final boolean agentLoaded;
    private final String userDir;
    private final String vmArgs;

    public VirtualMachine(String id, String displayName, boolean agentLoaded, String userDir, String vmArgs)
    {
        this.id = id;
        this.displayName = displayName;
        this.agentLoaded = agentLoaded;
        this.userDir = userDir;
        this.vmArgs = vmArgs;
    }

    public String getId()
    {
        return id;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public boolean isAgentLoaded()
    {
        return agentLoaded;
    }

    public LogSource getLogSourceFromVmArgs() throws CantReadFromSourceException
    {
        // Bail out if agentpath is not defined.
        if (vmArgs.indexOf(OPT_AGENT) < 0)
        {
            return getLogSource();
        }

        // Extract the value of the agentpath parameter
        
        int agentPathStart = vmArgs.indexOf(OPT_AGENT) + OPT_AGENT.length();
        
        // TODO FIX : if the logPath contains spaces, this will cause trouble.
        int agentPathEnd = vmArgs.indexOf(' ', agentPathStart);

        // If the agentpath is the last parameter in the VM Args, no space would be found.
        agentPathEnd = (agentPathEnd < 0) ? vmArgs.length() : agentPathEnd;

        String agentPath = vmArgs.substring(agentPathStart, agentPathEnd);
        
        // Bail out if logPath is not defined.
        if (agentPath.indexOf(OPT_LOG) < 0)
        {
            return getLogSource();
        }

        // Extract the value of the logPath parameter

        int logPathStart = agentPath.indexOf(OPT_LOG) + OPT_LOG.length();
        int commaPos = agentPath.indexOf(",", logPathStart);
        int spacePos = agentPath.indexOf(' ', logPathStart);
        int logPathEnd = commaPos > 0 ? commaPos : (spacePos > 0 ? spacePos : agentPath.length());

        File result = new File(agentPath.substring(logPathStart, logPathEnd));

        return result.exists() ? new FileLogSource(result) : getLogSource();
    }

    public LogSource getLogSource() throws CantReadFromSourceException
    {
        try
        {
            Path file = Files.list(Paths.get(userDir))
                .filter(this::isLogFile)
                .sorted()
                .findFirst()
                .orElseThrow(IOException::new);

            return new FileLogSource(file.toFile());
        }
        catch (IOException e)
        {
            throw new CantReadFromSourceException(e);
        }
    }

    private boolean isLogFile(final Path path)
    {
        String fileName = path.getFileName().toString();
        return fileName.endsWith(".hpl") && fileName.startsWith("log-" + id);
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        return Objects.equals(id, ((VirtualMachine) other).id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(id);
    }

    @Override
    public String toString()
    {
        return "VirtualMachine{" +
            "userDir='" + userDir + '\'' +
            ", agentLoaded=" + agentLoaded +
            ", displayName='" + displayName + '\'' +
            ", id='" + id + '\'' +
            '}';
    }

    @Override
    public int compareTo(VirtualMachine o)
    {
        return id.compareTo(o.getId());
    }

}
