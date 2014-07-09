package com.insightfullogic.honest_profiler.model.machines;

import java.io.File;
import java.util.Objects;

/**
 * Represents a Java Virtual Machine
 */
public class VirtualMachine {

    private final String id;
    private final String displayName;
    private final boolean agentLoaded;
    private final String userDir;

    public VirtualMachine(String id, String displayName, boolean agentLoaded, String userDir) {
        this.id = id;
        this.displayName = displayName;
        this.agentLoaded = agentLoaded;
        this.userDir = userDir;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isAgentLoaded() {
        return agentLoaded;
    }

    public File getLogFile() {
        return new File(userDir, "log.hpl");
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        return Objects.equals(id, ((VirtualMachine) other).id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
