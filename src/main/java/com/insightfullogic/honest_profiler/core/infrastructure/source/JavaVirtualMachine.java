package com.insightfullogic.honest_profiler.core.infrastructure.source;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class JavaVirtualMachine {

    private static final String VM_ARGS = "sun.jvm.args";
    private static final String AGENT_NAME = "liblagent.so";
    private static final String USER_DIR = "user.dir";

    private final String id;
    private final String displayName;

    private boolean agentLoaded;
    private String userDir;

    public JavaVirtualMachine(VirtualMachineDescriptor vmDescriptor) {
        this(vmDescriptor.id(), vmDescriptor.displayName());
        agentLoaded = checkAgentLoaded(vmDescriptor);
    }

    public JavaVirtualMachine(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
        this.agentLoaded = true;
    }

    private boolean checkAgentLoaded(VirtualMachineDescriptor vmDescriptor) {
        try {
            VirtualMachine vm = VirtualMachine.attach(vmDescriptor);
            Properties agentProperties = vm.getAgentProperties();
            String vmArgs = agentProperties.getProperty(VM_ARGS);
            userDir = agentProperties.getProperty(USER_DIR);
            return vmArgs.contains(AGENT_NAME);
        } catch (AttachNotSupportedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            if (!noSuchProcess(e)) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean noSuchProcess(IOException e) {
        return e.getMessage().contains("No such process");
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

        return Objects.equals(id, ((JavaVirtualMachine) other).id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
