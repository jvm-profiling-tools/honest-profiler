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
package com.insightfullogic.honest_profiler.ports.console;

import com.insightfullogic.honest_profiler.core.MachineListener;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.honest_profiler.ports.sources.LocalMachineSource;
import org.fusesource.jansi.Ansi.Color;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Character.isDigit;
import static org.fusesource.jansi.Ansi.Color.DEFAULT;
import static org.fusesource.jansi.Ansi.Color.GREEN;

public class MachinePickerScreen implements Screen, MachineListener {

    private final Terminal terminal;
    private final LocalMachineSource machineSource;
    private final List<VirtualMachine> machines;

    public MachinePickerScreen(Terminal terminal) {
        this.terminal = terminal;
        machineSource = new LocalMachineSource(LoggerFactory.getLogger(LocalMachineSource.class), this);
        machines = new ArrayList<>();
    }

    @Override
    public void handleInput(int input) {
        if (isDigit(input)) {
            try {
                VirtualMachine machine = machines.get(input);
                machineSource.stop();

                terminal.display(new ProfileScreen(machine, this, terminal));
            } catch (IndexOutOfBoundsException e) {
                // Just ignore numbers out of the range
            }
        }
    }

    @Override
    public void onDisplay() {
        renderAll();
        machineSource.start();
    }

    @Override
    public void onNewMachine(VirtualMachine machine) {
        machines.add(machine);
        if (isDisplayedScreen()) {
            render(machine);
        }
    }

    @Override
    public void onClosedMachine(VirtualMachine machine) {
        machines.remove(machine);
        if (isDisplayedScreen()) {
            renderAll();
        }
    }

    private void renderAll() {
        terminal.eraseScreen();
        renderHeader();
        machines.forEach(this::render);
    }

    private void renderHeader() {
        terminal.write(a -> a.bold().a("Virtual Machines:\n").boldOff());
    }

    private void render(VirtualMachine machine) {
        boolean agentLoaded = machine.isAgentLoaded();
        Color color = agentLoaded ? GREEN : DEFAULT;
        String prefix = agentLoaded ? machines.indexOf(machine) + ": " : " - ";
        terminal.write(a -> a.fg(color)
                .a(prefix)
                .a(machine.getDisplayName())
                .reset());
    }

    private boolean isDisplayedScreen() {
        return terminal.isDisplayedScreen(this);
    }

}
