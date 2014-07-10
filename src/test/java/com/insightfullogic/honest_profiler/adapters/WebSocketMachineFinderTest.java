package com.insightfullogic.honest_profiler.adapters;

import com.insightfullogic.honest_profiler.adapters.source.Messages;
import com.insightfullogic.honest_profiler.adapters.source.WebSocketMachineFinder;
import com.insightfullogic.honest_profiler.model.machines.MachineListener;
import com.insightfullogic.honest_profiler.model.machines.VirtualMachine;
import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.webbitserver.WebSocketConnection;

import java.util.HashSet;
import java.util.Set;

import static com.insightfullogic.lambdabehave.Suite.describe;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(JunitSuiteRunner.class)
public class WebSocketMachineFinderTest {{

    describe("Web Socket Machine Finder", it -> {

        byte[] message = Messages.NewMachine.newBuilder()
                .setDisplayName("com.intellij.idea.Main")
                .setId("123@erdos")
                .build()
                .toByteArray();

        VirtualMachine machine = new VirtualMachine("123@erdos", "com.intellij.idea.Main", true, "");
        WebSocketConnection connection = mock(WebSocketConnection.class);
        WebSocketMachineFinder finder = new WebSocketMachineFinder();
        MachineListener listener = mock(MachineListener.class);

        it.shouldSetup(() -> reset(listener));

        it.should("initially know of no machines", expect -> {
            when:
            finder.poll(listener);

            then:
            verify(listener).update(new HashSet<>(), new HashSet<>());
        });

        it.should("recognise a new machine", expect -> {
            doAnswer(listener, (added, removed) -> {
                expect.that(added).hasItem(machine);
                expect.that(removed).isEmpty();
            });

            when:
            finder.onMessage(connection, message);
            finder.poll(listener);

            then:
            verify(listener).update(any(), any());
        });

        it.should("recognise a removed machine", expect -> {
            doAnswer(listener, (added, removed) -> {
                expect.that(removed).hasItem(machine);
                expect.that(added).isEmpty();
            });

            given:
            finder.onMessage(connection, message);

            when:
            finder.onClose(connection);
            finder.poll(listener);

            then:
            verify(listener).update(any(), any());
        });

    });
}

    private static void doAnswer(MachineListener listener, MachineListener callback) {
        Mockito.doAnswer(invoke -> {
            Set<VirtualMachine> added = (Set<VirtualMachine>) invoke.getArguments()[0];
            Set<VirtualMachine> removed = (Set<VirtualMachine>) invoke.getArguments()[1];

            callback.update(added, removed);
            return null;
        }).when(listener).update(any(), any());
    }

}
