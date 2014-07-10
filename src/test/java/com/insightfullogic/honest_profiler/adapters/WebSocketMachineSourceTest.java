package com.insightfullogic.honest_profiler.adapters;

import com.insightfullogic.honest_profiler.adapters.sources.Messages;
import com.insightfullogic.honest_profiler.adapters.sources.WebSocketMachineSource;
import com.insightfullogic.honest_profiler.core.conductor.Conductor;
import com.insightfullogic.honest_profiler.core.conductor.LogConsumer;
import com.insightfullogic.honest_profiler.core.conductor.MachineListener;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.webbitserver.WebSocketConnection;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

import static com.insightfullogic.lambdabehave.Suite.describe;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(JunitSuiteRunner.class)
public class WebSocketMachineSourceTest {{

    describe("Web Socket Machine Finder", it -> {

        byte[] newMachine = Messages.NewMachine.newBuilder()
                .setDisplayName("com.intellij.idea.Main")
                .setId("123@erdos")
                .build()
                .toByteArray();

        VirtualMachine machine = new VirtualMachine("123@erdos", "com.intellij.idea.Main", true, "");
        WebSocketConnection connection = mock(WebSocketConnection.class);
        LogConsumer logConsumer = mock(LogConsumer.class);
        Conductor conductor = mock(Conductor.class);
        WebSocketMachineSource finder = new WebSocketMachineSource(conductor);
        MachineListener listener = mock(MachineListener.class);

        it.shouldSetup(() -> {
            reset(connection, logConsumer, conductor, listener);
            when(conductor.onNewLog(any())).thenReturn(logConsumer);
            when(logConsumer.getMachine()).thenReturn(machine);
        });

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
            finder.onMessage(connection, newMachine);
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
            finder.onMessage(connection, newMachine);

            when:
            finder.onClose(connection);
            finder.poll(listener);

            then:
            verify(listener).update(any(), any());
        });

        it.should("cope with connections being opened and closed erratically", expect -> {
            given:
            finder.onOpen(connection);

            when:
            finder.onClose(connection);
            finder.poll(listener);

            then:
            verify(listener).update(new HashSet<>(), new HashSet<>());
        });

        it.should("cope with invalid data", expect -> {
            when:
            finder.onMessage(connection, new byte[]{});
            finder.poll(listener);

            then:
            verify(listener).update(new HashSet<>(), new HashSet<>());
        });

        it.should("send data to the log consumer", expect -> {
            given:
            finder.onMessage(connection, newMachine);
            byte[] data = {0, 1, 2, 3};

            when:
            finder.onMessage(connection, data);

            then:
            verify(logConsumer).accept(ByteBuffer.wrap(data));
        });

    });
}

    @SuppressWarnings("unchecked")
    private static void doAnswer(MachineListener listener, MachineListener callback) {
        Mockito.doAnswer(invoke -> {
            Set<VirtualMachine> added = (Set<VirtualMachine>) invoke.getArguments()[0];
            Set<VirtualMachine> removed = (Set<VirtualMachine>) invoke.getArguments()[1];

            callback.update(added, removed);
            return null;
        }).when(listener).update(any(), any());
    }

}
