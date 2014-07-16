package com.insightfullogic.honest_profiler.adapters;

import com.insightfullogic.honest_profiler.adapters.sources.Messages;
import com.insightfullogic.honest_profiler.adapters.sources.WebSocketMachineSource;
import com.insightfullogic.honest_profiler.core.conductor.Conductor;
import com.insightfullogic.honest_profiler.core.conductor.DataConsumer;
import com.insightfullogic.honest_profiler.core.conductor.MachineListener;
import com.insightfullogic.honest_profiler.core.conductor.ProfileListener;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import org.junit.runner.RunWith;
import org.webbitserver.WebSocketConnection;

import java.nio.ByteBuffer;

import static com.insightfullogic.lambdabehave.Suite.describe;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(JunitSuiteRunner.class)
public class WebSocketMachineSourceTest {

    WebSocketMachineSource finder;

    { describe("Web Socket Machine Finder", it -> {

        byte[] newMachine = Messages.NewMachine.newBuilder()
                .setDisplayName("com.intellij.idea.Main")
                .setId("123@erdos")
                .build()
                .toByteArray();

        VirtualMachine machine = new VirtualMachine("123@erdos", "com.intellij.idea.Main", true, "");
        WebSocketConnection connection = mock(WebSocketConnection.class);
        DataConsumer dataConsumer = mock(DataConsumer.class);
        Conductor conductor = mock(Conductor.class);
        MachineListener listener = mock(MachineListener.class);
        ProfileListener profileListener = mock(ProfileListener.class);

        it.shouldSetup(() -> {
            reset(connection, dataConsumer, conductor, listener);
            when(conductor.pipeData(any(), any())).thenReturn(dataConsumer);
            when(dataConsumer.getMachine()).thenReturn(machine);
            when(listener.onNewMachine(any())).thenReturn(profileListener);

            finder = new WebSocketMachineSource(conductor, listener);
        });

        it.should("initially know of no machines", expect -> {
            verifyNoMoreInteractions(listener);
        });

        it.should("recognise a new machine", expect -> {
            when:
            finder.onMessage(connection, newMachine);

            then:
            verify(listener).onNewMachine(machine);
            verifyNoMoreInteractions(listener);
        });

        it.should("recognise a removed machine", expect -> {
            given:
            finder.onMessage(connection, newMachine);
            verify(listener).onNewMachine(machine);

            when:
            finder.onClose(connection);

            then:
            verify(listener).onClosedMachine(machine);
            verifyNoMoreInteractions(listener);
        });

        it.should("cope with connections being opened and closed erratically", expect -> {
            given:
            finder.onOpen(connection);

            when:
            finder.onClose(connection);

            then:
            verifyNoMoreInteractions(listener);
        });

        it.should("cope with invalid data", expect -> {
            when:
            finder.onMessage(connection, new byte[]{});

            then:
            verifyNoMoreInteractions(listener);
        });

        it.should("send data to the log consumer", expect -> {
            given:
            finder.onMessage(connection, newMachine);
            byte[] data = {0, 1, 2, 3};

            when:
            finder.onMessage(connection, data);

            then:
            verify(dataConsumer).accept(ByteBuffer.wrap(data));
        });

    });
}}
