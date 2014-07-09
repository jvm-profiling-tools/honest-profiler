package com.insightfullogic.honest_profiler.adapters;

import com.insightfullogic.honest_profiler.adapters.source.Messages;
import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import org.junit.runner.RunWith;

import static com.insightfullogic.lambdabehave.Suite.describe;

@RunWith(JunitSuiteRunner.class)
public class WebSocketMachineFinderTest {{

    describe("Web Socket Machine Finder", it -> {

        byte[] message = Messages.NewMachine.newBuilder()
                .setDisplayName("com.intellij.idea.Main")
                .setId("123@erdos")
                .build()
                .toByteArray();

        it.should("TODO", expect -> {



        });

    });

}}
