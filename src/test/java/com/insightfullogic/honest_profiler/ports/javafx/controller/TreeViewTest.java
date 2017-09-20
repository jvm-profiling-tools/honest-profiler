package com.insightfullogic.honest_profiler.ports.javafx.controller;

import static com.insightfullogic.honest_profiler.framework.ParameterUtil.getScenariosAndGroupings;
import static com.insightfullogic.honest_profiler.ports.javafx.ViewType.TREE;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.clickExpandAll;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.getTreeTableView;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.newProfileTab;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.selectFrameGrouping;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.selectThreadGrouping;
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
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.framework.checker.TreeTableViewCheckAdapter;
import com.insightfullogic.honest_profiler.framework.scenario.SimplifiedLogScenario;
import com.insightfullogic.honest_profiler.ports.javafx.framework.AbstractJavaFxTest;

import javafx.scene.control.TreeTableView;

@RunWith(Parameterized.class)
public class TreeViewTest extends AbstractJavaFxTest
{
    @Parameters(name = "{0} : <{1},{2}>")
    public static Collection<Object[]> data()
    {
        return getScenariosAndGroupings();
    }

    // Instance Properties

    private SimplifiedLogScenario scenario;
    private ThreadGrouping threadGrouping;
    private FrameGrouping frameGrouping;

    // Instance Constructors

    public TreeViewTest(SimplifiedLogScenario scenario,
                        ThreadGrouping threadGrouping,
                        FrameGrouping frameGrouping)
    {
        this.scenario = scenario;
        this.threadGrouping = threadGrouping;
        this.frameGrouping = frameGrouping;
    }

    // Actual Test Method

    @Test
    public void testTreeViewScenario()
    {
        FxRobot robot = new FxRobot();

        newProfileTab(robot, app(), 0, scenario.getName(), scenario, LOG);

        selectView(robot, TREE);
        selectThreadGrouping(robot, threadGrouping, "#tree");
        selectFrameGrouping(robot, frameGrouping, "#tree");

        clickExpandAll(robot, "#tree");

        TreeTableView<Node> tableView = getTreeTableView(robot);

        runLater(() -> scenario.checkTreeAggregation(
            new TreeTableViewCheckAdapter(threadGrouping, frameGrouping, tableView)));
    }
}
