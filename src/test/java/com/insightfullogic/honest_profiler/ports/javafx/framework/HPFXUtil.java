package com.insightfullogic.honest_profiler.ports.javafx.framework;

import static java.util.concurrent.TimeUnit.SECONDS;
import static javafx.application.Platform.runLater;
import static javafx.scene.input.MouseButton.PRIMARY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testfx.util.WaitForAsyncUtils.waitFor;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import org.testfx.api.FxRobot;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.framework.generator.ProfileContextGenerator;
import com.insightfullogic.honest_profiler.framework.scenario.LogScenario;
import com.insightfullogic.honest_profiler.ports.javafx.JavaFXApplication;
import com.insightfullogic.honest_profiler.ports.javafx.ViewType;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext.ProfileMode;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;

public class HPFXUtil
{
    // Headless ?

    public static boolean isHeadless()
    {
        return Boolean.getBoolean("headless");
    }

    // Profile Creation

    public static final void newProfileTab(FxRobot robot, JavaFXApplication application,
        int expectedIndex, String name, LogScenario scenario, ProfileMode mode)
    {
        TabPane tabPane = robot.lookup("#profileTabs").query();
        wait(() -> tabPane.isVisible());

        assertEquals(expectedIndex, tabPane.getTabs().size());

        ProfileContextGenerator gen = new ProfileContextGenerator(application, name, mode);
        gen.createNewProfile(scenario);

        wait(() -> tabPane.getTabs().size() == expectedIndex + 1);

        Tab tab = tabPane.getTabs().get(expectedIndex);

        wait(
            () -> ((Parent)tab.getGraphic()).getChildrenUnmodifiable().stream()
                .filter(node -> node instanceof Label && name.equals(((Label)node).getText()))
                .findFirst().isPresent());
    }

    // Aggregation View Retrieval

    public static TableView<Entry> getFlatTableView(FxRobot robot)
    {
        TableView<Entry> result = robot.from(robot.lookup("#flat")).lookup("#flatTable").query();
        wait(() -> result.isVisible());
        return result;
    }

    public static TreeTableView<Node> getTreeTableView(FxRobot robot)
    {
        TreeTableView<Node> result = robot.from(robot.lookup("#tree")).lookup("#treeTable").query();
        wait(() -> result.isVisible());
        wait(() -> result.getRoot() != null);
        return result;
    }

    // Button Clicking

    public static void clickExpandAll(FxRobot robot, String contextId)
    {
        Button button = robot.from(robot.lookup(contextId)).lookup("#expandAllButton").query();

        wait(() -> button.isVisible() && !button.isDisabled());

        if (isHeadless())
        {
            button.fire();
        }
        else
        {
            robot.clickOn(button, PRIMARY);
        }
    }

    // ChoiceBox Selection

    public static <T> void selectChoice(FxRobot robot, T choice, String fxId, String contextId)
    {
        if (!isHeadless())
        {
            robot.clickOn(fxId).clickOn(choice.toString());
            return;
        }

        ChoiceBox<T> choiceBox;
        if (contextId == null)
        {
            choiceBox = robot.lookup(fxId).query();
        }
        else
        {
            wait(() -> robot.lookup(contextId).query() != null);
            javafx.scene.Node context = robot.lookup(contextId).query();
            choiceBox = robot.from(context).lookup(fxId).query();
        }

        wait(() -> choiceBox.isVisible() && !choiceBox.isDisabled());
        runLater(() -> choiceBox.getSelectionModel().select(choice));
        wait(() -> choiceBox.getSelectionModel().getSelectedItem() == choice);
    }

    public static void selectView(FxRobot robot, ViewType viewType)
    {
        selectChoice(robot, viewType, "#viewChoice", null);
        switch (viewType)
        {
            case FLAT:
                wait(() -> robot.lookup("#flat").query() != null);
                wait(() -> robot.lookup("#flat").query().isVisible());
                return;
            case TREE:
                wait(() -> robot.lookup("#tree").query() != null);
                wait(() -> robot.lookup("#tree").query().isVisible());
                return;
            case FLAME:
                wait(() -> robot.lookup("#flame").query() != null);
                wait(() -> robot.lookup("#flame").query().isVisible());
                return;
            default:
                break;
        }
    }

    public static void selectThreadGrouping(FxRobot robot, ThreadGrouping threadGrouping,
        String contextId)
    {
        selectChoice(robot, threadGrouping, "#threadGrouping", contextId);
    }

    public static void selectFrameGrouping(FxRobot robot, FrameGrouping frameGrouping,
        String contextIdId)
    {
        selectChoice(robot, frameGrouping, "#frameGrouping", contextIdId);
    }

    public static void wait(Callable<Boolean> condition)
    {
        wait(condition, null);
    }

    public static void wait(Callable<Boolean> condition, String failureMessage)
    {
        try
        {
            waitFor(10, SECONDS, condition);
        }
        catch (TimeoutException toe)
        {
            toe.printStackTrace();
            fail(
                (failureMessage == null ? "A Timeout occurred : " : failureMessage + " : ")
                    + toe.getClass().getCanonicalName()
                    + " : "
                    + toe.getMessage());
        }
    }
}
