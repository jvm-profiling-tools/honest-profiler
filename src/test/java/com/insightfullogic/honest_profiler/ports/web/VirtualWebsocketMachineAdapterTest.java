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
package com.insightfullogic.honest_profiler.ports.web;

import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static com.insightfullogic.lambdabehave.Suite.describe;
import static org.mockito.Mockito.verify;

@RunWith(JunitSuiteRunner.class)
public class VirtualWebsocketMachineAdapterTest
{

    ClientConnections clients;
    VirtualMachine jvm = new VirtualMachine("12432", "com.intellij.idea.Main", true, "", "");
    WebsocketMachineAdapter adapter;

    {
        describe("The Java Virtual Machine Adapter", it -> {

            it.isSetupWith(() -> {
                clients = Mockito.mock(ClientConnections.class);
                adapter = new WebsocketMachineAdapter(clients, new MessageEncoder());
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

    }
}
