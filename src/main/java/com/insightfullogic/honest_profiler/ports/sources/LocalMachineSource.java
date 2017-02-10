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
package com.insightfullogic.honest_profiler.ports.sources;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.insightfullogic.honest_profiler.core.MachineListener;
import com.insightfullogic.honest_profiler.core.ThreadedAgent;
import com.insightfullogic.honest_profiler.core.platform.Platforms;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachineDescriptor;

public class LocalMachineSource
{
    private static final String VM_ARGS = "sun.jvm.args";
    private static final String AGENT_NAME = "liblagent" + Platforms.getDynamicLibraryExtension();
    private static final String USER_DIR = "user.dir";
    private static final long DEFAULT_SLEEP_PERIOD = 500;

    private final Logger logger;
    private MachineListener listener;
    private final long sleepPeriod;
    private final ThreadedAgent threadedAgent;

    private Map<VirtualMachineDescriptor, VirtualMachine> vmMap;

    public LocalMachineSource(final Logger logger, final MachineListener listener)
    {
        this(logger, listener, DEFAULT_SLEEP_PERIOD);
    }

    public LocalMachineSource(final Logger logger,
                              final MachineListener listener,
                              final long sleepPeriod)
    {
        this.logger = logger;
        this.listener = listener;
        this.sleepPeriod = sleepPeriod;
        vmMap = new HashMap<>();
        threadedAgent = new ThreadedAgent(
            LoggerFactory.getLogger(ThreadedAgent.class),
            this::discoverVirtualMachines);
    }

    @PostConstruct
    public void start()
    {
        threadedAgent.start();
    }

    public boolean discoverVirtualMachines()
    {
        poll();

        return sleep();
    }

    private boolean sleep()
    {
        try
        {
            Thread.sleep(sleepPeriod);
            return true;
        }
        catch (InterruptedException e)
        {
            return false;
        }
    }

    private void poll()
    {
        Set<VirtualMachineDescriptor> current = new HashSet<>(com.sun.tools.attach.VirtualMachine.list());
        difference(current, vmMap.keySet(), this::onNewDescriptor);
        difference(new HashSet<>(vmMap.keySet()), current, this::onClosedDescriptor);
    }

    private void difference(
        Set<VirtualMachineDescriptor> left,
        Set<VirtualMachineDescriptor> right,
        Consumer<VirtualMachineDescriptor> action)
    {

        // TODO: only attach once per vm
        left.stream()
            .filter(vm -> !right.contains(vm))
            .forEach(action);
    }
    
    private void onNewDescriptor(VirtualMachineDescriptor descriptor)
    {
        VirtualMachine vm = attach(descriptor);
        if(vm != null) {
            vmMap.put(descriptor, vm);
            listener.onNewMachine(vm);
        }
    }

    private void onClosedDescriptor(VirtualMachineDescriptor descriptor)
    {
        VirtualMachine vm = vmMap.remove(descriptor);
        if(vm != null) {
            listener.onClosedMachine(vm);
        }
    }

    private VirtualMachine attach(VirtualMachineDescriptor descriptor)
    {
        try
        {
            com.sun.tools.attach.VirtualMachine vm = com.sun.tools.attach.VirtualMachine.attach(descriptor);

            String vmArgs = vm.getAgentProperties().getProperty(VM_ARGS);
            String id = descriptor.id();
            String displayName = descriptor.displayName();
            boolean agentLoaded = vmArgs.contains(AGENT_NAME);
            String userDir = getUserDir(vm);
          
            return new VirtualMachine(id, displayName, agentLoaded, userDir, vmArgs);
        }
        catch (AttachNotSupportedException e)
        {
            logger.warn(e.getMessage());
        }
        catch (IOException e)
        {
            if (!noSuchProcess(e))
            {
                logger.warn(e.getMessage(), e);
            }
        }
        return null;
    }

    private String getUserDir(com.sun.tools.attach.VirtualMachine vm) throws IOException
    {
        final String userDir = vm.getAgentProperties().getProperty(USER_DIR);
        if (userDir != null)
        {
            return userDir;
        }

        return vm.getSystemProperties().getProperty(USER_DIR);
    }

    private boolean noSuchProcess(IOException e)
    {
        return e.getMessage().contains("No such process");
    }

    @PreDestroy
    public void stop()
    {
        threadedAgent.stop();
    }
}
