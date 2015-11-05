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
package com.insightfullogic.honest_profiler.ports.javafx.landing;

import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import javafx.geometry.Insets;
import javafx.scene.control.RadioButton;
import javafx.scene.text.Font;

import static javafx.geometry.NodeOrientation.LEFT_TO_RIGHT;
import static javafx.scene.control.ContentDisplay.RIGHT;
import static javafx.scene.text.TextAlignment.CENTER;

public class MachineButton extends RadioButton
{

    private static final Font font = new Font("Bitstream Vera Sans Bold", 16);

    private final VirtualMachine jvm;

    public MachineButton(VirtualMachine jvm)
    {
        super(jvm.getDisplayName());
        this.jvm = jvm;
        setDisable(!jvm.isAgentLoaded());
        setId(jvm.getId());
        setButtonStyling();
    }

    public VirtualMachine getJvm()
    {
        return jvm;
    }

    private void setButtonStyling()
    {
        setContentDisplay(RIGHT);
        setNodeOrientation(LEFT_TO_RIGHT);
        setPrefHeight(22);
        setPrefWidth(507);
        setTextAlignment(CENTER);
        setFont(font);
        setPadding(new Insets(20, 0, 0, 100));
    }

}
