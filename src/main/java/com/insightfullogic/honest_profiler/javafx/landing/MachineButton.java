package com.insightfullogic.honest_profiler.javafx.landing;

import com.insightfullogic.honest_profiler.discovery.JavaVirtualMachine;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import javafx.geometry.Insets;
import javafx.scene.control.RadioButton;
import javafx.scene.text.Font;

import java.io.IOException;
import java.util.Properties;

import static javafx.geometry.NodeOrientation.LEFT_TO_RIGHT;
import static javafx.scene.control.ContentDisplay.RIGHT;
import static javafx.scene.text.TextAlignment.CENTER;

public class MachineButton extends RadioButton {

    private static final Font font = new Font("Bitstream Vera Sans Bold", 16);

    private final JavaVirtualMachine jvm;

    public MachineButton(JavaVirtualMachine jvm) {
        super(jvm.getDisplayName());
        this.jvm = jvm;
        setDisable(!jvm.isAgentLoaded());
        setId(jvm.getId());
        setButtonStyling();
    }

    public JavaVirtualMachine getJvm() {
        return jvm;
    }

    private void setButtonStyling() {
        setContentDisplay(RIGHT);
        setNodeOrientation(LEFT_TO_RIGHT);
        setPrefHeight(22);
        setPrefWidth(507);
        setTextAlignment(CENTER);
        setFont(font);
        setPadding(new Insets(20, 0, 0, 100));
    }

}
