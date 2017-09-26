package com.insightfullogic.honest_profiler.ports.javafx.controller;

import static com.insightfullogic.honest_profiler.framework.ParameterUtil.getScenarios;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.newProfileTab;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.waitUntil;
import static com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext.ProfileMode.LOG;
import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.testfx.api.FxRobot;

import com.insightfullogic.honest_profiler.framework.scenario.SimplifiedLogScenario;
import com.insightfullogic.honest_profiler.ports.javafx.framework.AbstractJavaFxTest;

import javafx.scene.control.Label;

@RunWith(Parameterized.class)
public class SampleCountTest extends AbstractJavaFxTest
{
    // Class Methods

    @Parameters(name = "{0}")
    public static Collection<Object[]> data()
    {
        return getScenarios();
    }

    // Instance Properties

    private SimplifiedLogScenario scenario;

    // Instance Constructors

    public SampleCountTest(SimplifiedLogScenario scenario)
    {
        this.scenario = scenario;
    }

    // Actual Test Method

    @Test
    public void testSampleCount()
    {
        FxRobot robot = new FxRobot();

        newProfileTab(robot, app(), 0, scenario.getName(), scenario, LOG);

        Label label = robot.lookup("#profileSampleCount").query();
        waitUntil(() -> label.getText() != null && !label.getText().isEmpty());

        assertEquals(scenario.getTraceCount() + " samples", label.getText());
    }
}
