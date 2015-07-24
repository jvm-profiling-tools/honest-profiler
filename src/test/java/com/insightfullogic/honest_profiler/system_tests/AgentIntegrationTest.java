/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
package com.insightfullogic.honest_profiler.system_tests;

import com.insightfullogic.honest_profiler.core.MachineListener;
import com.insightfullogic.honest_profiler.core.Monitor;
import com.insightfullogic.honest_profiler.core.collector.Profile;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.honest_profiler.ports.sources.LocalMachineSource;
import com.insightfullogic.honest_profiler.testing_utilities.AgentRunner;
import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import com.insightfullogic.lambdabehave.expectations.Expect;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static com.insightfullogic.lambdabehave.Suite.describe;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.concurrent.locks.LockSupport.parkNanos;

@RunWith(JunitSuiteRunner.class)
public class AgentIntegrationTest {{

    describe("Agent Integration", it -> {

        it.should("should result in a monitorable JVM", expect -> {
            AgentRunner.run("InfiniteExample", runner -> {
                int seenTraceCount = 0;

                AtomicReference<Profile> lastProfile = new AtomicReference<>();
                parkNanos(SECONDS.toNanos(1));

                new LocalMachineSource(logger, new MachineListener() {
                    @Override
                    public void onNewMachine(final VirtualMachine machine) {
                        if (machine.isAgentLoaded()) {
                            Monitor.pipeFile(machine.getLogSource(), lastProfile::set);
                        }
                    }

                    @Override
                    public void onClosedMachine(final VirtualMachine machine) {

                    }
                }).discoverVirtualMachines();

                seenTraceCount = expectIncreasingTraceCount(expect, seenTraceCount, lastProfile);

                seenTraceCount = expectIncreasingTraceCount(expect, seenTraceCount, lastProfile);

                seenTraceCount = expectIncreasingTraceCount(expect, seenTraceCount, lastProfile);
            });
        });
    });

    describe("Agent start/stop", it -> {

        it.should("should result in a monitorable JVM", expect -> {
            AgentRunner.run("InfiniteExample", "start=0", runner -> {
                int seenTraceCount = 0;

                AtomicReference<Profile> lastProfile = new AtomicReference<>();
                parkNanos(SECONDS.toNanos(1));

                new LocalMachineSource(logger, new MachineListener() {
                    @Override
                    public void onNewMachine(final VirtualMachine machine) {
                        if (machine.isAgentLoaded()) {
                            Monitor.pipeFile(machine.getLogSource(), lastProfile::set);
                        }
                    }

                    @Override
                    public void onClosedMachine(final VirtualMachine machine) {

                    }
                }).discoverVirtualMachines();

                seenTraceCount = expectNonIncreasingTraceCount(expect, seenTraceCount, lastProfile);
                try {
                    runner.startProfiler();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                seenTraceCount = expectIncreasingTraceCount(expect, seenTraceCount, lastProfile);
                try {
                    runner.stopProfiler();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                seenTraceCount = expectIncreasingTraceCount(expect, seenTraceCount, lastProfile);
                seenTraceCount = expectNonIncreasingTraceCount(expect, seenTraceCount, lastProfile);

            });
        });
    });

}

    private static Logger logger = LoggerFactory.getLogger(AgentIntegrationTest.class);

    int expectIncreasingTraceCount(Expect expect, int seenTraceCount, AtomicReference<Profile> lastProfile) {
        logger.debug("Last seen trace count is {}", seenTraceCount);
        parkNanos(SECONDS.toNanos(1));
        Profile profile = lastProfile.get();
        int currentTraceCount = profile == null ? 0 : profile.getTraceCount();
        expect.that(currentTraceCount).isGreaterThan(seenTraceCount);
        return currentTraceCount;
    }

    int expectNonIncreasingTraceCount(Expect expect, int seenTraceCount, AtomicReference<Profile> lastProfile) {
        logger.debug("Last seen trace count is {}", seenTraceCount);
        parkNanos(SECONDS.toNanos(1));
        Profile profile = lastProfile.get();
        int currentTraceCount = profile == null ? 0 : profile.getTraceCount();
        expect.that(currentTraceCount).isEqualTo(seenTraceCount);
        return currentTraceCount;
    }
}
