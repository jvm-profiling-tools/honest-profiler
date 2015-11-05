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
package com.insightfullogic.honest_profiler.ports.console;

import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import org.fusesource.jansi.Ansi;

import java.util.List;

import static org.fusesource.jansi.Ansi.Color.DEFAULT;
import static org.fusesource.jansi.Ansi.Color.GREEN;

public class MachinePickerView
{

    private final Terminal terminal;

    public MachinePickerView(final Terminal terminal)
    {
        this.terminal = terminal;
    }

    public void renderAll(final List<VirtualMachine> machines)
    {
        terminal.eraseScreen();
        renderHeader();
        for (int i = 0; i < machines.size(); i++)
        {
            render(machines.get(i), i);
        }
    }

    public void renderHeader()
    {
        terminal.write(a -> a.bold().a("Virtual Machines:\n").boldOff());
    }

    public void render(final VirtualMachine machine, final int index)
    {
        boolean agentLoaded = machine.isAgentLoaded();
        Ansi.Color color = agentLoaded ? GREEN : DEFAULT;
        String prefix = agentLoaded ? index + ": " : " - ";
        terminal.write(a -> a.fg(color)
            .a(prefix)
            .a(machine.getDisplayName())
            .reset());
    }

}
