package com.insightfullogic.honest_profiler.delivery.web;

import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static com.insightfullogic.lambdabehave.Suite.describe;
import static org.mockito.Mockito.verify;

@RunWith(JunitSuiteRunner.class)
public class VirtualMachineAdapterTest {

    ClientConnections clients;
    VirtualMachine jvm = new VirtualMachine("12432", "com.intellij.idea.Main", true, "");
    MachineAdapter adapter;

    { describe("The Java Virtual Machine Adapter", it -> {

        it.shouldSetup(() -> {
            clients = Mockito.mock(ClientConnections.class);
            adapter = new MachineAdapter(clients, new MessageEncoder());
        });

        it.should("Register for connections", expect -> {
            verify(clients).setListener(adapter);
        });

        it.should("send information about added JVMs", expect -> {
            adapter.onNewMachine(jvm);

            verify(clients).sendAll("{ \"type\": \"addJvm\", \"machine\": { \"name\": \"com.intellij.idea.Main\", \"id\": \"12432\", \"agent\": true } }");
        });

        it.should("send information about removed JVMs", expect -> {
            adapter.onClosedMachine(jvm);

            verify(clients).sendAll("{ \"type\": \"removeJvm\", \"id\": \"12432\" }");
        });

    });

}}
