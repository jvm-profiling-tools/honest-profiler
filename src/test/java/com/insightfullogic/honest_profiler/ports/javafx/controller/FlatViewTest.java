package com.insightfullogic.honest_profiler.ports.javafx.controller;

import static com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping.BY_ID;
import static com.insightfullogic.honest_profiler.framework.ParameterUtil.getScenariosAndFrameGroupings;
import static com.insightfullogic.honest_profiler.ports.javafx.ViewType.FLAT;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.getFlatTableView;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.newProfileTab;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.selectFrameGrouping;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.selectView;
import static com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext.ProfileMode.LOG;
import static javafx.application.Platform.runLater;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.testfx.api.FxRobot;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.framework.checker.FlatTableViewCheckAdapter;
import com.insightfullogic.honest_profiler.framework.scenario.SimplifiedLogScenario;
import com.insightfullogic.honest_profiler.ports.javafx.framework.AbstractJavaFxTest;

import javafx.scene.control.TableView;

@RunWith(Parameterized.class)
public class FlatViewTest extends AbstractJavaFxTest
{
    // Class Methods

    @Parameters(name = "{0} : <{1}>")
    public static Collection<Object[]> data()
    {
        return getScenariosAndFrameGroupings();
    }

    // Instance Properties

    private SimplifiedLogScenario scenario;
    private FrameGrouping frameGrouping;

    // Instance Constructors

    public FlatViewTest(SimplifiedLogScenario scenario, FrameGrouping frameGrouping)
    {
        this.scenario = scenario;
        this.frameGrouping = frameGrouping;
    }

    // Actual Test Method

    @Test
    public void testFlatViewScenario()
    {
        FxRobot robot = new FxRobot();

        newProfileTab(robot, app(), 0, scenario.getName(), scenario, LOG);

        selectView(robot, FLAT);
        selectFrameGrouping(robot, frameGrouping, "#flat");

        TableView<Entry> tableView = getFlatTableView(robot);

        runLater(() -> scenario.checkFlatAggregation(
            new FlatTableViewCheckAdapter(BY_ID, frameGrouping, tableView)));
    }
}
