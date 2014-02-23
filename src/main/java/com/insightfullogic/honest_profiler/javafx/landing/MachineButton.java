package com.insightfullogic.honest_profiler.javafx.landing;

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

    private static final String VM_ARGS = "sun.jvm.args";

    private static final Font font = new Font("Bitstream Vera Sans Bold", 16);
    private static final String AGENT_NAME = "liblagent.so";
    public static final String USER_DIR = "user.dir";

    private String userDir;

    public MachineButton(VirtualMachineDescriptor vmDescriptor) {
        super(vmDescriptor.displayName());
        setDisable(true);
        setId(vmDescriptor.id());
        setButtonStyling();
        checkRunningAnAgent(vmDescriptor);
    }

    private void checkRunningAnAgent(VirtualMachineDescriptor vmDescriptor) {
        try {
            VirtualMachine vm = VirtualMachine.attach(vmDescriptor);
            Properties agentProperties = vm.getAgentProperties();
            String vmArgs = agentProperties.getProperty(VM_ARGS);
            boolean canConnect = vmArgs.contains(AGENT_NAME);
            setDisable(!canConnect);
            userDir = agentProperties.getProperty(USER_DIR);
        } catch (AttachNotSupportedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public String getUserDir() {
        return userDir;
    }

}
