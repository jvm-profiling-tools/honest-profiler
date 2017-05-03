package com.insightfullogic.honest_profiler.ports.javafx.controller;

import static com.insightfullogic.honest_profiler.framework.ParameterUtil.getDiffScenariosAndGroupings;
import static com.insightfullogic.honest_profiler.ports.javafx.ViewType.TREE;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.clickExpandAll;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.getTreeDiffTableView;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.newProfileTab;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.selectCtxMenu;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.selectFrameGrouping;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.selectTab;
import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.selectThreadGrouping;
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
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.DiffNode;
import com.insightfullogic.honest_profiler.framework.checker.TreeDiffTableViewCheckAdapter;
import com.insightfullogic.honest_profiler.framework.scenario.SimplifiedLogScenario;
import com.insightfullogic.honest_profiler.ports.javafx.framework.AbstractJavaFxTest;

import javafx.scene.control.TreeTableView;

@RunWith(Parameterized.class)
@Ignore
public class TreeDiffViewTest extends AbstractJavaFxTest
{
    // Class Methods

    @Parameters(name = "{0} - {1} : <{2},{3}>")
    public static Collection<Object[]> data()
    {
        return getDiffScenariosAndGroupings();
    }

    // Instance Properties

    private SimplifiedLogScenario baseScenario;
    private SimplifiedLogScenario newScenario;
    private ThreadGrouping threadGrouping;
    private FrameGrouping frameGrouping;

    // Instance Constructors

    public TreeDiffViewTest(SimplifiedLogScenario baseScenario,
                            SimplifiedLogScenario newScenario,
                            ThreadGrouping threadGrouping,
                            FrameGrouping frameGrouping)
    {
        this.baseScenario = baseScenario;
        this.newScenario = newScenario;
        this.threadGrouping = threadGrouping;
        this.frameGrouping = frameGrouping;
    }

    // Actual Test Method

    @Test
    public void testTreeDiffViewScenario()
    {
        FxRobot robot = new FxRobot();

        waitUntil(asyncFx(() -> getMainStage().setMaximized(true)));

        newProfileTab(robot, app(), 0, "Base : " + baseScenario.getName(), baseScenario, LOG);
        newProfileTab(robot, app(), 1, "New : " + newScenario.getName(), newScenario, LOG);
        selectTab(robot, 1);
        selectCtxMenu(robot, "#compareButton", 0, "Base : " + baseScenario.getName());
        selectTab(robot, 2);
        selectView(robot, TREE);
        selectFrameGrouping(robot, frameGrouping, "#tree");
        selectThreadGrouping(robot, threadGrouping, "#tree");

        clickExpandAll(robot, "#tree");

        TreeTableView<DiffNode> tableView = getTreeDiffTableView(robot);

        newScenario.checkTreeDiffAggregation(
            baseScenario,
            new TreeDiffTableViewCheckAdapter(threadGrouping, frameGrouping, tableView));
    }
}
