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
package com.insightfullogic.honest_profiler.system_tests;

import com.insightfullogic.honest_profiler.core.MachineListener;
import com.insightfullogic.honest_profiler.core.Monitor;
import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.honest_profiler.ports.sources.FileLogSource;
import com.insightfullogic.honest_profiler.ports.sources.LocalMachineSource;
import com.insightfullogic.honest_profiler.testing_utilities.AgentRunner;
import com.insightfullogic.honest_profiler.testing_utilities.NonInteractiveAgentRunner;
import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import com.insightfullogic.lambdabehave.expectations.Expect;
import org.hamcrest.Matcher;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

import static com.insightfullogic.lambdabehave.Suite.describe;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.concurrent.locks.LockSupport.parkNanos;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

@RunWith(JunitSuiteRunner.class)
public class AgentIntegrationTest
{

    private static Logger logger = LoggerFactory.getLogger(AgentIntegrationTest.class);

    private AtomicReference<FileLogSource> file = new AtomicReference<>();

    {

        describe("Agent Integration", it -> {

            it.isConcludedWith(() ->
            {
                final FileLogSource source = file.get();
                if (source != null)
                {
                    source.getFile().delete();
                }
            });

            it.should("should result in a monitorable JVM", expect ->
            {
                AgentRunner.run("InfiniteExample", AgentRunner.DEFAULT_AGENT_INTERVAL,
                        runner ->
                {
                    int seenTraceCount = 0;

                    AtomicReference<Profile> lastProfile = discoverVirtualMachines();

                    seenTraceCount = expectIncreasingTraceCount(expect, seenTraceCount, lastProfile);

                    seenTraceCount = expectIncreasingTraceCount(expect, seenTraceCount, lastProfile);

                    seenTraceCount = expectIncreasingTraceCount(expect, seenTraceCount, lastProfile);
                });
            });

            it.should("should be able to start/stop the JVM", expect ->
            {
                AgentRunner.run("InfiniteExample", new String[] { "start=0", AgentRunner.DEFAULT_AGENT_INTERVAL },
                        runner ->
                {
                    int seenTraceCount = 0;

                    AtomicReference<Profile> lastProfile = discoverVirtualMachines();

                    seenTraceCount = expectNonIncreasingTraceCount(expect, seenTraceCount, lastProfile);

                    runner.startProfiler();
                    
                    seenTraceCount = expectIncreasingTraceCount(expect, seenTraceCount, lastProfile);

                    runner.stopProfiler();

                    seenTraceCount = expectIncreasingTraceCount(expect, seenTraceCount, lastProfile);
                    seenTraceCount = expectNonIncreasingTraceCount(expect, seenTraceCount, lastProfile);
                });
            });

            it.should("should be able to change agent settings", expect ->
            {
                NonInteractiveAgentRunner.run("AgentApiExample", "start=0", runner ->
                {
                    expect.that(runner.isSuccessful()).is(equalTo(true));

                    if (!runner.isSuccessful())
                        logger.debug("Errors in agent profiled process {}", runner.getErrorMessages());
                });
            });
        });

    }


    private AtomicReference<Profile> discoverVirtualMachines()
    {
        AtomicReference<Profile> lastProfile = new AtomicReference<>();
        parkNanos(SECONDS.toNanos(1));

        new LocalMachineSource(logger, new MachineListener()
        {
            @Override
            public void onNewMachine(final VirtualMachine machine)
            {
                if (machine.isAgentLoaded())
                {
                    final FileLogSource logSource = (FileLogSource) machine.getLogSource();
                    file.set(logSource);
                    Monitor.pipeFile(logSource, lastProfile::set);
                }
            }

            @Override
            public void onClosedMachine(final VirtualMachine machine)
            {

            }
        }).discoverVirtualMachines();

        return lastProfile;
    }

    private int expectIncreasingTraceCount(Expect expect, int seenTraceCount, AtomicReference<Profile> lastProfile)
    {
        return expectTraceCount(expect, seenTraceCount, greaterThan(seenTraceCount), lastProfile);
    }

    private int expectNonIncreasingTraceCount(Expect expect, int seenTraceCount, AtomicReference<Profile> lastProfile)
    {
        return expectTraceCount(expect, seenTraceCount, equalTo(seenTraceCount), lastProfile);
    }

    private int expectTraceCount(final Expect expect,
                                 final int seenTraceCount,
                                 final Matcher<Integer> matcher,
                                 final AtomicReference<Profile> lastProfile)
    {
        logger.debug("Last seen trace count is {}", seenTraceCount);
        parkNanos(SECONDS.toNanos(1));
        Profile profile = lastProfile.get();
        int currentTraceCount = profile == null ? 0 : profile.getTraceCount();
        expect.that(currentTraceCount).is(matcher);
        return currentTraceCount;
    }
}
