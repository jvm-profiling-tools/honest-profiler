package com.insightfullogic.honest_profiler.ports.javafx.framework;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.testfx.util.WaitForAsyncUtils.waitFor;

import java.util.concurrent.TimeoutException;

import org.testfx.api.FxRobot;

import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.framework.generator.ProfileContextGenerator;
import com.insightfullogic.honest_profiler.framework.scenario.LogScenario;
import com.insightfullogic.honest_profiler.ports.javafx.JavaFXApplication;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext.ProfileMode;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;

public class HPFXUtil
{
    public static final void newProfileTab(FxRobot robot, JavaFXApplication application,
        int expectedIndex, String name, LogScenario scenario, ProfileMode mode)
    {
        try
        {
            TabPane tabPane = robot.lookup("#profileTabs").query();
            waitFor(10, SECONDS, () -> tabPane.isVisible());
            assertEquals(expectedIndex, tabPane.getTabs().size());

            ProfileContextGenerator gen = new ProfileContextGenerator(application, name, mode);
            gen.createNewProfile(scenario);

            waitFor(10, SECONDS, () -> tabPane.getTabs().size() == expectedIndex + 1);
            Tab tab = tabPane.getTabs().get(expectedIndex);
            waitFor(
                10,
                SECONDS,
                () -> ((Parent)tab.getGraphic()).getChildrenUnmodifiable().stream()
                    .filter(node -> node instanceof Label && name.equals(((Label)node).getText()))
                    .findFirst().isPresent());
        }
        catch (TimeoutException toe)
        {
            toe.printStackTrace();
            fail(toe.getClass().getCanonicalName() + " : " + toe.getMessage());
        }
    }

    public static TableView<?> getFlatTableView(FxRobot robot)
    {
        try
        {
            waitFor(
                10,
                SECONDS,
                () -> robot.from(robot.lookup("#flat")).lookup("#flatTable").query() != null);

            TableView<?> tableView = robot.from(robot.lookup("#flat")).lookup("#flatTable").query();
            assertNotNull(tableView);

            return tableView;
        }
        catch (TimeoutException toe)
        {
            toe.printStackTrace();
            fail(toe.getClass().getCanonicalName() + " : " + toe.getMessage());
            return null;
        }
    }

    public static TreeTableView<Node> getTreeTableView(FxRobot robot)
    {
        try
        {
            waitFor(
                10,
                SECONDS,
                () -> robot.from(robot.lookup("#tree")).lookup("#treeTable").query() != null);

            TreeTableView<Node> tableView = robot.from(robot.lookup("#tree")).lookup("#treeTable")
                .query();
            assertNotNull(tableView);

            waitFor(10, SECONDS, () -> tableView.getRoot() != null);

            return tableView;
        }
        catch (TimeoutException toe)
        {
            toe.printStackTrace();
            fail(toe.getClass().getCanonicalName() + " : " + toe.getMessage());
            return null;
        }
    }
}
