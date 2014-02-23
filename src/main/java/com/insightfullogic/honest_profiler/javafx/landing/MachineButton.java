package com.insightfullogic.honest_profiler.javafx.landing;

import com.sun.tools.attach.VirtualMachineDescriptor;
import javafx.geometry.Insets;
import javafx.scene.control.RadioButton;
import javafx.scene.text.Font;

import static javafx.geometry.NodeOrientation.LEFT_TO_RIGHT;
import static javafx.scene.control.ContentDisplay.RIGHT;
import static javafx.scene.text.TextAlignment.CENTER;

public class MachineButton extends RadioButton {

    private static final Font font = new Font("Bitstream Vera Sans Bold", 16);

    public MachineButton(VirtualMachineDescriptor vm) {
        super(vm.displayName());
        setId(vm.id());

        setContentDisplay(RIGHT);
        setNodeOrientation(LEFT_TO_RIGHT);
        setPrefHeight(22);
        setPrefWidth(507);
        setTextAlignment(CENTER);
        setFont(font);
        setPadding(new Insets(20, 0, 0, 100));
    }

}
