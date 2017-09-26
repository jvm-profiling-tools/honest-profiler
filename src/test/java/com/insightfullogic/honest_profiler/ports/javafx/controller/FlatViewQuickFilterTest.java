package com.insightfullogic.honest_profiler.ports.javafx.controller;

import static com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping.ALL_TOGETHER;
import static com.insightfullogic.honest_profiler.framework.ParameterUtil.getScenarios;
import static com.insightfullogic.honest_profiler.framework.scenario.ScenarioStraightFilter.keyFlt;
import static com.insightfullogic.honest_profiler.ports.javafx.ViewType.FLAT;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.clickQuickFilterButton;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.focusOn;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.getFlatTableView;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.newProfileTab;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.selectFrameGrouping;
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
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.framework.checker.FlatTableViewCheckAdapter;
import com.insightfullogic.honest_profiler.framework.scenario.ScenarioStraightFilter;
import com.insightfullogic.honest_profiler.framework.scenario.SimplifiedLogScenario;
import com.insightfullogic.honest_profiler.ports.javafx.framework.AbstractJavaFxTest;

import javafx.scene.control.TableView;

@Ignore
// TODO: identify why this is failing
// is it a missing UI change from @PhRX
@RunWith(Parameterized.class)
public class FlatViewQuickFilterTest extends AbstractJavaFxTest
{
    @Parameters(name = "{0}")
    public static Collection<Object[]> data()
    {
        return getScenarios();
    }

    private SimplifiedLogScenario scenario;

    public FlatViewQuickFilterTest(SimplifiedLogScenario scenario)
    {
        this.scenario = scenario;
    }

    @Test
    public void testFlatQuickFilterScenario()
    {
        FxRobot robot = new FxRobot();

        newProfileTab(robot, app(), 0, scenario.getName(), scenario, LOG);

        selectView(robot, FLAT);
        focusOn(robot, "#quickFilterText", "#flat");
        robot.write("ba");
        clickQuickFilterButton(robot, "#flat");

        checkResult(robot, keyFlt(s -> s.contains("ba")));

        selectView(robot, FLAT);
        focusOn(robot, "#quickFilterText", "#flat");
        robot.type(BACK_SPACE, BACK_SPACE, ENTER);

        checkResult(robot);
    }

    private void checkResult(FxRobot robot, ScenarioStraightFilter... filters)
    {
        asList(FrameGrouping.values()).forEach(fg ->
        {
            selectFrameGrouping(robot, fg, "#flat");

            TableView<Entry> tableView = getFlatTableView(robot);

            scenario.checkFlatAggregation(
                new FlatTableViewCheckAdapter(ALL_TOGETHER, fg, tableView),
                filters);
        });
    }
}
