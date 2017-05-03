package com.insightfullogic.honest_profiler.ports.javafx.controller;

import static com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping.BY_ID;
import static com.insightfullogic.honest_profiler.framework.ParameterUtil.getDiffScenariosAndFrameGroupings;
import static com.insightfullogic.honest_profiler.ports.javafx.ViewType.FLAT;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.getFlatDiffTableView;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.newProfileTab;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.selectCtxMenu;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.selectFrameGrouping;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.selectTab;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.selectView;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.waitUntil;
import static com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext.ProfileMode.LOG;
import static org.testfx.util.WaitForAsyncUtils.asyncFx;

import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.testfx.api.FxRobot;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.DiffEntry;
import com.insightfullogic.honest_profiler.framework.checker.FlatDiffTableViewCheckAdapter;
import com.insightfullogic.honest_profiler.framework.scenario.SimplifiedLogScenario;
import com.insightfullogic.honest_profiler.ports.javafx.framework.AbstractJavaFxTest;

import javafx.scene.control.TableView;

@RunWith(Parameterized.class)
@Ignore
public class FlatDiffViewTest extends AbstractJavaFxTest
{
    // Class Methods

    @Parameters(name = "{0} - {1} : <{2}>")
    public static Collection<Object[]> data()
    {
        return getDiffScenariosAndFrameGroupings();
    }

    // Instance Properties

    private SimplifiedLogScenario baseScenario;
    private SimplifiedLogScenario newScenario;
    private FrameGrouping frameGrouping;

    // Instance Constructors

    public FlatDiffViewTest(SimplifiedLogScenario baseScenario,
                            SimplifiedLogScenario newScenario,
                            FrameGrouping frameGrouping)
    {
        this.baseScenario = baseScenario;
        this.newScenario = newScenario;
        this.frameGrouping = frameGrouping;
    }

    // Actual Test Method

    @Test
    public void testFlatDiffViewScenario()
    {
        FxRobot robot = new FxRobot();

        waitUntil(asyncFx(() -> getMainStage().setMaximized(true)));

        newProfileTab(robot, app(), 0, "Base : " + baseScenario.getName(), baseScenario, LOG);
        newProfileTab(robot, app(), 1, "New : " + newScenario.getName(), newScenario, LOG);
        selectTab(robot, 1);
        selectCtxMenu(robot, "#compareButton", 0, "Base : " + baseScenario.getName());
        selectTab(robot, 2);
        selectView(robot, FLAT);
        selectFrameGrouping(robot, frameGrouping, "#flat");

        TableView<DiffEntry> tableView = getFlatDiffTableView(robot);

        newScenario.checkFlatDiffAggregation(
            baseScenario,
            new FlatDiffTableViewCheckAdapter(BY_ID, frameGrouping, tableView));
    }
}
