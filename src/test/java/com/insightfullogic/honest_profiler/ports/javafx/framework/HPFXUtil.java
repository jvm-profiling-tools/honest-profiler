package com.insightfullogic.honest_profiler.ports.javafx.framework;

import static java.lang.Boolean.getBoolean;
import static java.util.concurrent.TimeUnit.SECONDS;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.MouseButton.PRIMARY;
import static javafx.scene.input.MouseButton.SECONDARY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testfx.util.WaitForAsyncUtils.asyncFx;
import static org.testfx.util.WaitForAsyncUtils.waitFor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import org.testfx.api.FxRobot;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.DiffEntry;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.DiffNode;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.framework.generator.ProfileContextGenerator;
import com.insightfullogic.honest_profiler.framework.scenario.LogScenario;
import com.insightfullogic.honest_profiler.ports.javafx.JavaFXApplication;
import com.insightfullogic.honest_profiler.ports.javafx.ViewType;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext.ProfileMode;

import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class HPFXUtil
{
    // Headless ?

    public static boolean isHeadless()
    {
        return getBoolean("headless");
    }

    // Profile Creation

    public static final void newProfileTab(FxRobot robot, JavaFXApplication application,
        int expectedIndex, String name, LogScenario scenario, ProfileMode mode)
    {
        TabPane tabPane = robot.lookup("#profileTabs").query();
        waitUntil(() -> tabPane.isVisible());

        assertEquals(expectedIndex, tabPane.getTabs().size());

        ProfileContextGenerator gen = new ProfileContextGenerator(application, name, mode);
        waitUntil(asyncFx(() -> gen.createNewProfile(scenario)));

        waitUntil(() -> tabPane.getTabs().size() == expectedIndex + 1);

        Tab tab = tabPane.getTabs().get(expectedIndex);

        waitUntil(
            () -> ((Parent)tab.getGraphic()).getChildrenUnmodifiable().stream()
                .filter(node -> node instanceof Label && name.equals(((Label)node).getText()))
                .findFirst().isPresent());
    }

    // Aggregation View Retrieval

    public static TableView<Entry> getFlatTableView(FxRobot robot)
    {
        javafx.scene.Node context = getContext(robot, "#flat");
        TableView<Entry> result = robot.from(context).lookup("#flatTable").query();
        waitUntil(() -> result.isVisible());
        return result;
    }

    public static TreeTableView<Node> getTreeTableView(FxRobot robot)
    {
        javafx.scene.Node context = getContext(robot, "#tree");
        TreeTableView<Node> result = robot.from(context).lookup("#treeTable").query();
        waitUntil(() -> result.isVisible());
        waitUntil(() -> result.getRoot() != null);
        return result;
    }

    public static TableView<DiffEntry> getFlatDiffTableView(FxRobot robot)
    {
        TableView<DiffEntry> result = robot.lookup("#flatDiffTable").query();
        waitUntil(() -> result != null && result.isVisible());
        return result;
    }

    public static TreeTableView<DiffNode> getTreeDiffTableView(FxRobot robot)
    {
        TreeTableView<DiffNode> result = robot.lookup("#treeDiffTable").query();
        waitUntil(() -> result.isVisible());
        waitUntil(() -> result.getRoot() != null);
        return result;
    }

    // Clicking

    public static void clickButton(FxRobot robot, String nodeId, String contextId)
    {
        javafx.scene.Node context = getContext(robot, contextId);
        Button button = robot.from(context).lookup(nodeId).query();

        waitUntil(() -> button.isVisible() && !button.isDisabled());

        if (isHeadless())
        {
            waitUntil(asyncFx(() -> button.fire()));
        }
        else
        {
            robot.clickOn(button, PRIMARY);
        }
    }

    public static void clickExpandAll(FxRobot robot, String contextId)
    {
        clickButton(robot, "#expandAllButton", contextId);
    }

    public static void clickQuickFilterButton(FxRobot robot, String contextId)
    {
        clickButton(robot, "#quickFilterButton", contextId);
    }

    public static void focusOn(FxRobot robot, String nodeId, String contextId)
    {
        javafx.scene.Node context = getContext(robot, contextId);
        javafx.scene.Node node = robot.from(context).lookup(nodeId).query();

        waitUntil(() -> node.isVisible() && !node.isDisabled());

        if (isHeadless())
        {
            waitUntil(asyncFx(() -> node.requestFocus()));
            waitUntil(asyncFx(() -> node.isFocused()));
        }
        else
        {
            robot.clickOn(node, PRIMARY);
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
            javafx.scene.Node context = getContext(robot, contextId);
            choiceBox = robot.from(context).lookup(fxId).query();
        }

        waitUntil(() -> choiceBox.isVisible() && !choiceBox.isDisabled());
        waitUntil(asyncFx(() -> choiceBox.getSelectionModel().select(choice)));
    }

    public static void selectView(FxRobot robot, ViewType viewType)
    {
        selectChoice(robot, viewType, "#viewChoice", null);
        switch (viewType)
        {
            case FLAT:
                waitUntil(() -> robot.lookup("#flat").query() != null);
                waitUntil(() -> robot.lookup("#flat").query().isVisible());
                return;
            case TREE:
                waitUntil(() -> robot.lookup("#tree").query() != null);
                waitUntil(() -> robot.lookup("#tree").query().isVisible());
                return;
            case FLAME:
                waitUntil(() -> robot.lookup("#flame").query() != null);
                waitUntil(() -> robot.lookup("#flame").query().isVisible());
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
        String contextId)
    {
        selectChoice(robot, frameGrouping, "#frameGrouping", contextId);
    }

    // Tab Selection

    public static void selectTab(FxRobot robot, int index)
    {
        TabPane tabPane = robot.lookup("#profileTabs").query();
        waitUntil(() -> tabPane.isVisible());
        waitUntil(() -> tabPane.getTabs().size() >= index + 1);
        waitUntil(asyncFx(() -> tabPane.getSelectionModel().select(index)));
        waitUntil(
            () -> tabPane.getTabs().get(index).isSelected()
                && tabPane.getTabs().get(index).getContent().isVisible());
    }

    // Context Menu Selection

    public static void selectCtxMenu(FxRobot robot, String triggerId, int itemIndex,
        String expectedName)
    {
        javafx.scene.Node node = robot.lookup(triggerId).query();
        waitUntil(() -> node.isVisible() && !node.isDisabled());

        if (isHeadless())
        {
            EventHandler<? super MouseEvent> handler = ((Control)node).onMousePressedProperty()
                .get();
            waitUntil(
                asyncFx(() -> handler.handle(
                    new MouseEvent(
                        MouseEvent.MOUSE_CLICKED,
                        0,
                        0,
                        0,
                        0,
                        MouseButton.PRIMARY,
                        1,
                        true,
                        true,
                        true,
                        true,
                        true,
                        true,
                        true,
                        true,
                        true,
                        true,
                        null)
                ))
            );
            waitUntil(() -> ((Control)node).getContextMenu() != null);

            ContextMenu menu = waitUntil(asyncFx(() -> ((Control)node).getContextMenu()));
            MenuItem item = waitUntil(asyncFx(() -> (menu.getItems().get(itemIndex))));
            assertEquals("Wrong ContextMenu MenuItem", expectedName, item.getText());
            waitUntil(asyncFx(() -> item.fire()));
        }
        else
        {
            robot.clickOn(node, SECONDARY);
            for (int i = 0; i < itemIndex + 1; i++)
            {
                robot.type(DOWN);
            }
            robot.type(ENTER);
        }
    }

    // Context Lookup

    public static javafx.scene.Node getContext(FxRobot robot, String contextId)
    {
        waitUntil(() -> robot.lookup(contextId).query() != null);
        return robot.lookup(contextId).query();
    }

    // waitFor() Wrappers

    public static void waitUntil(Callable<Boolean> condition)
    {
        waitUntil(condition, null);
    }

    public static void waitUntil(Callable<Boolean> condition, String failureMessage)
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

    public static <T> T waitUntil(Future<T> future)
    {
        return waitUntil(future, null);
    }

    public static <T> T waitUntil(Future<T> future, String failureMessage)
    {
        try
        {
            return waitFor(10, SECONDS, future);
        }
        catch (TimeoutException toe)
        {
            toe.printStackTrace();
            fail(
                (failureMessage == null ? "A Timeout occurred : " : failureMessage + " : ")
                    + toe.getClass().getCanonicalName()
                    + " : "
                    + toe.getMessage());
            return null;
        }
    }
}
