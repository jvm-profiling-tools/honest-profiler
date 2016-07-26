package com.insightfullogic.honest_profiler.ports.sources;

import com.insightfullogic.honest_profiler.core.MachineListener;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.honest_profiler.testing_utilities.AgentRunner;
import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import static com.insightfullogic.lambdabehave.Suite.describe;
import static org.mockito.Mockito.mock;

@RunWith(JunitSuiteRunner.class)
public class LocalMachineSourceTest
{
    {

        describe("Local Machine Sources", it -> {

            Logger logger = mock(Logger.class);


            it.should("detect local machines", expect -> {
                AgentRunner.run("InfiniteExample", AgentRunner.DEFAULT_AGENT_INTERVAL, runner -> {
                    final int expectedProcessId = runner.getProcessId();
                    new LocalMachineSource(logger, new MachineListener()
                    {
                        @Override
                        public void onNewMachine(final VirtualMachine machine)
                        {
                            int machineProcessId = Integer.parseInt(machine.getId());
                            expect.that(machine.isAgentLoaded()).is(machineProcessId == expectedProcessId);
                        }

                        @Override
                        public void onClosedMachine(final VirtualMachine machine)
                        {
                            expect.failure("Should never close VM " + machine);
                        }
                    }).discoverVirtualMachines();
                });
            });

            it.should("detect no local machines if none are running", expect -> {
                new LocalMachineSource(logger, new MachineListener()
                {
                    @Override
                    public void onNewMachine(final VirtualMachine machine)
                    {
                        expect.that(machine.isAgentLoaded()).is(false);
                    }

                    @Override
                    public void onClosedMachine(final VirtualMachine machine)
                    {
                        expect.failure("Should never close VM " + machine);
                    }
                }).discoverVirtualMachines();
            });

        });

    }}
