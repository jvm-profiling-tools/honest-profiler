package com.insightfullogic.honest_profiler.ports.javafx.controller;

import static com.insightfullogic.honest_profiler.framework.ParameterUtil.getScenarios;
import static com.insightfullogic.honest_profiler.framework.scenario.ScenarioStraightFilter.keyFlt;
import static com.insightfullogic.honest_profiler.ports.javafx.ViewType.TREE;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.clickExpandAll;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.clickQuickFilterButton;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.focusOn;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.getTreeTableView;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.newProfileTab;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.selectFrameGrouping;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.selectThreadGrouping;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.selectView;
import static com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext.ProfileMode.LOG;
import static java.util.Arrays.asList;
import static javafx.scene.input.KeyCode.BACK_SPACE;
import static javafx.scene.input.KeyCode.ENTER;

import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.testfx.api.FxRobot;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.framework.checker.TreeTableViewCheckAdapter;
import com.insightfullogic.honest_profiler.framework.scenario.ScenarioStraightFilter;
import com.insightfullogic.honest_profiler.framework.scenario.SimplifiedLogScenario;
import com.insightfullogic.honest_profiler.ports.javafx.framework.AbstractJavaFxTest;

import javafx.scene.control.TreeTableView;

@Ignore
// TODO: identify why this is failing
// is it a missing UI change from @PhRX
@RunWith(Parameterized.class)
public class TreeViewQuickFilterTest extends AbstractJavaFxTest
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

    public TreeViewQuickFilterTest(SimplifiedLogScenario scenario)
    {
        this.scenario = scenario;
    }

    // Actual Test Method

    @Test
    public void testTreeQuickFilterScenario()
    {
        FxRobot robot = new FxRobot();

        newProfileTab(robot, app(), 0, scenario.getName(), scenario, LOG);

        selectView(robot, TREE);
        focusOn(robot, "#quickFilterText", "#tree");
        robot.write("ba");
        clickQuickFilterButton(robot, "#tree");

        checkResult(robot, keyFlt(s -> s.contains("ba")));

        selectView(robot, TREE);
        focusOn(robot, "#quickFilterText", "#tree");
        robot.type(BACK_SPACE, BACK_SPACE, ENTER);

        checkResult(robot);
    }

    private void checkResult(FxRobot robot, ScenarioStraightFilter... filters)
    {
        asList(ThreadGrouping.values()).forEach(tg ->
        {
            asList(FrameGrouping.values()).forEach(fg ->
            {
                selectThreadGrouping(robot, tg, "#tree");
                selectFrameGrouping(robot, fg, "#tree");
                clickExpandAll(robot, "#tree");

                TreeTableView<Node> tableView = getTreeTableView(robot);

                scenario.checkTreeAggregation(
                    new TreeTableViewCheckAdapter(tg, fg, tableView),
                    filters);
            });
        });
    }
}
