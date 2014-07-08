package com.insightfullogic.honest_profiler.delivery.web;

import com.insightfullogic.honest_profiler.core.infrastructure.source.JavaVirtualMachine;
import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static com.insightfullogic.lambdabehave.Suite.describe;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.mockito.Mockito.verify;

@RunWith(JunitSuiteRunner.class)
public class VirtualMachineAdapterTest {

    Connections connections;
    JavaVirtualMachine jvm = new JavaVirtualMachine("12432", "com.intellij.idea.Main");
    VirtualMachineAdapter adapter;

    { describe("The Java Virtual Machine Adapter", it -> {

        it.shouldSetup(() -> {
            connections = Mockito.mock(Connections.class);
            adapter = new VirtualMachineAdapter(connections, new MessageEncoder());
        });

        it.should("Register for connections", expect -> {
            verify(connections).setListener(adapter);
        });

        it.should("send information about added JVMs", expect -> {
            adapter.update(singleton(jvm), emptySet());

            verify(connections).sendAll("{ \"type\": \"addJvm\", \"machine\": { \"name\": \"com.intellij.idea.Main\", \"id\": \"12432\" } }");
        });

        it.should("send information about removed JVMs", expect -> {
            adapter.update(emptySet(), singleton(jvm));

            verify(connections).sendAll("{ \"type\": \"removeJvm\", \"id\": \"12432\" }");
        });

    });

}}
