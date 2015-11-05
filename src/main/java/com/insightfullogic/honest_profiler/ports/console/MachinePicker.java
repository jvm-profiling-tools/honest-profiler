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

import com.insightfullogic.honest_profiler.core.MachineListener;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.honest_profiler.ports.sources.LocalMachineSource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.lang.Character.isDigit;

public class MachinePicker implements Screen, MachineListener
{

    private final LocalMachineSource machineSource;
    private final List<VirtualMachine> machines;
    private final MachinePickerView view;
    private final Terminal terminal;

    public MachinePicker(Terminal terminal, Function<MachineListener, LocalMachineSource> machineSourceFactory)
    {
        this(new MachinePickerView(terminal), terminal, machineSourceFactory);
    }

    public MachinePicker(
        MachinePickerView view,
        Terminal terminal,
        Function<MachineListener, LocalMachineSource> machineSourceFactory)
    {

        this.view = view;
        this.terminal = terminal;
        machineSource = machineSourceFactory.apply(this);
        machines = new ArrayList<>();
    }

    @Override
    public void handleInput(int input)
    {
        if (isDigit(input))
        {
            try
            {
                int index = Character.getNumericValue(input);
                System.out.println(input);
                System.out.println(index);
                VirtualMachine machine = machines.get(index);

                terminal.display(new ProfileScreen(machine, this, terminal));
            }
            catch (IndexOutOfBoundsException e)
            {
                // Just ignore numbers out of the range
            }
        }
    }

    @Override
    public void onShow()
    {
        view.renderAll(machines);
        machineSource.start();
    }

    @Override
    public void onHide()
    {
        machineSource.stop();
    }

    @Override
    public void onNewMachine(VirtualMachine machine)
    {
        machines.add(machine);
        if (isDisplayedScreen())
        {
            view.render(machine, machines.size() - 1);
        }
    }

    @Override
    public void onClosedMachine(VirtualMachine machine)
    {
        machines.remove(machine);
        if (isDisplayedScreen())
        {
            view.renderAll(machines);
        }
    }

    private boolean isDisplayedScreen()
    {
        return terminal.isDisplayedScreen(this);
    }

}
