/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **/
package com.insightfullogic.honest_profiler.ports.javafx.controller;

import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType.THREAD_SAMPLE;
import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType.TIME_SHARE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ContextMenuUtil.bindContextMenuForTreeCell;
import static com.insightfullogic.honest_profiler.ports.javafx.util.DialogUtil.FILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.COLLAPSE_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.EXPAND_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.FUNNEL_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.FUNNEL_ACTIVE_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.viewFor;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Rendering.renderMethod;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Rendering.renderPercentage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.insightfullogic.honest_profiler.core.filters.ProfileFilter;
import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.core.profiles.ProfileNode;
import com.insightfullogic.honest_profiler.core.profiles.ProfileTree;
import com.insightfullogic.honest_profiler.ports.javafx.controller.filter.FilterDialogController;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterSpecification;
import com.insightfullogic.honest_profiler.ports.javafx.util.DialogUtil;
import com.insightfullogic.honest_profiler.ports.javafx.util.TreeUtil;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.TreeViewCell;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

public class TreeViewController
{
    private static final double TIMESHARE_EXPAND_FACTOR = 0.2;

    @FXML
    private Button filterButton;
    @FXML
    private Button expandAllButton;
    @FXML
    private Button collapseAllButton;

    @FXML
    private TreeTableView<ProfileNode> treeView;
    @FXML
    private TreeTableColumn<ProfileNode, String> methodColumn;
    @FXML
    private TreeTableColumn<ProfileNode, ProfileNode> percentColumn;
    @FXML
    private TreeTableColumn<ProfileNode, String> totalColumn;
    @FXML
    private TreeTableColumn<ProfileNode, String> selfColumn;

    private FilterDialogController filterDialogController;
    private ObjectProperty<FilterSpecification> filterSpec;

    private ProfileContext profileContext;
    private ProfileFilter currentFilter;

    private final RootNodeAdapter rootNode = new RootNodeAdapter();

    @FXML
    private void initialize()
    {
        currentFilter = new ProfileFilter();

        filterSpec = new SimpleObjectProperty<>(new FilterSpecification());
        filterSpec.addListener((property, oldValue, newValue) ->
        {
            filterButton.setGraphic(
                newValue == null || !newValue.isFiltering() ? viewFor(FUNNEL_16)
                    : viewFor(FUNNEL_ACTIVE_16));
            currentFilter = new ProfileFilter(newValue.getFilters());
            refresh(profileContext.getProfile());
        });

        filterDialogController = (FilterDialogController) DialogUtil
            .<FilterSpecification>createDialog(FILTER, "Specify Filters", false);
        filterDialogController.addAllowedFilterTypes(THREAD_SAMPLE, TIME_SHARE);

        filterButton.setGraphic(viewFor(FUNNEL_16));
        filterButton.setTooltip(new Tooltip("Specify filters"));
        filterButton.setOnAction(
            event -> filterSpec.set(filterDialogController.showAndWait().get()));

        expandAllButton.setGraphic(viewFor(EXPAND_16));
        expandAllButton.setTooltip(new Tooltip("Expand all threads"));
        expandAllButton.setOnAction(
            event -> treeView.getRoot().getChildren().stream().forEach(TreeUtil::expandFully));

        collapseAllButton.setGraphic(viewFor(COLLAPSE_16));
        collapseAllButton.setTooltip(new Tooltip("Collapse all threads"));
        collapseAllButton.setOnAction(
            event -> treeView.getRoot().getChildren().stream()
                .forEach(TreeUtil::collapseFully));

        treeView.setRoot(rootNode);

        totalColumn.setCellValueFactory(
            param -> new ReadOnlyStringWrapper(
                param.getValue().getValue() != null
                    ? renderPercentage(param.getValue().getValue().getTotalTimeShare()) : ""));
        selfColumn.setCellValueFactory(
            param -> new ReadOnlyStringWrapper(
                param.getValue().getValue() != null
                    ? renderPercentage(param.getValue().getValue().getSelfTimeShare()) : ""));

        methodColumn.setCellFactory(column -> new ProfileNodeCell());
        methodColumn.setCellValueFactory(param ->
        {
            TreeItem<ProfileNode> treeItem = param.getValue();
            String text = "";

            if (treeItem instanceof ThreadNodeAdapter)
            {
                ThreadNodeAdapter adapter = (ThreadNodeAdapter) treeItem;
                String name = adapter.getThreadName();

                StringBuilder builder = new StringBuilder("Thread").append(' ');
                if (adapter.getThreadId() < 0)
                {
                    builder.append("Unknown [" + adapter.getThreadId() + "]");
                }
                else
                {
                    builder.append(adapter.getThreadId());
                }

                builder.append(' ')
                    .append((name == null || name.isEmpty()) ? "Unknown" : "[" + name + "]")
                    .append(" (depth = ").append(adapter.getDepth()).append(", # samples = ")
                    .append(adapter.getNrOfSamples()).append(")");

                text = builder.toString();
            }
            else if (treeItem instanceof MethodNodeAdapter)
            {
                text = renderMethod(treeItem.getValue().getFrameInfo());
            }

            return new ReadOnlyStringWrapper(text);
        });

        percentColumn.setCellFactory(param -> new TreeViewCell());
    }

    public void setProfileContext(ProfileContext profileContext)
    {
        this.profileContext = profileContext;
        profileContext.profileProperty()
            .addListener((property, oldValue, newValue) -> refresh(newValue));
    }

    // Helper Methods

    private void refresh(Profile profile)
    {
        Profile newProfile = profile.copy();
        currentFilter.accept(newProfile);
        rootNode.update(newProfile.getTrees());
    }

    // Helper Classes

    private class ProfileNodeCell extends TreeTableCell<ProfileNode, String>
    {
        public ProfileNodeCell()
        {
            super();
            bindContextMenuForTreeCell(this);
        }

        @Override
        protected void updateItem(String item, boolean empty)
        {
            super.updateItem(item, empty);

            setStyle(null);
            if (empty)
            {
                setText(null);
                setGraphic(null);
                return;
            }
            setText(item);
        }
    }

    private class RootNodeAdapter extends TreeItem<ProfileNode>
    {
        private final Map<Long, ThreadNodeAdapter> threadsById;

        public RootNodeAdapter()
        {
            threadsById = new HashMap<>();
            setExpanded(true);
        }

        public void update(List<ProfileTree> trees)
        {
            ObservableList<TreeItem<ProfileNode>> children = getChildren();

            boolean hideErrorThreads = filterSpec.get()
                .isHideErrorThreads();

            Set<Long> present = new HashSet<>();
            for (ProfileTree tree : trees)
            {
                if (hideErrorThreads
                    && tree.getRootNode().getFrameInfo().getMethodId() < 0
                    && tree.getRootNode().getChildren().isEmpty())
                {
                    continue; // Skip. Won't be marked as present either.
                }

                present.add(tree.getThreadId());

                long threadId = tree.getThreadId();
                ThreadNodeAdapter thread = threadsById.computeIfAbsent(threadId, id ->
                {
                    ThreadNodeAdapter adapter = new ThreadNodeAdapter(
                        id,
                        tree.getThreadName(),
                        tree.getNumberOfSamples());
                    children.add(adapter);
                    return adapter;
                });

                thread.update(tree);
            }

            threadsById.keySet().stream().filter(key -> !present.contains(key))
                .forEach(key -> children.remove(threadsById.get(key)));
            threadsById.keySet().retainAll(present);
        }
    }

    private static class ThreadNodeAdapter extends TreeItem<ProfileNode>
    {
        private final long threadId;
        private final String threadName;
        private final MethodNodeAdapter adapter;
        private long nrOfSamples;

        public ThreadNodeAdapter(long threadId, String threadName, long nrOfSamples)
        {
            this.threadId = threadId;
            this.threadName = threadName;
            this.nrOfSamples = nrOfSamples;
            adapter = new MethodNodeAdapter();
            setExpanded(true);
            getChildren().add(adapter);
        }

        public void update(ProfileTree tree)
        {
            nrOfSamples = tree.getNumberOfSamples();
            adapter.update(tree.getRootNode());
        }

        public long getThreadId()
        {
            return threadId;
        }

        public String getThreadName()
        {
            return threadName;
        }

        public long getNrOfSamples()
        {
            return nrOfSamples;
        }

        public int getDepth()
        {
            return adapter.getDepth() + 1;
        }
    }

    public static class MethodNodeAdapter extends TreeItem<ProfileNode>
    {
        private final Map<Long, MethodNodeAdapter> childrenByMethodId;

        private boolean firstUpdate;

        public MethodNodeAdapter()
        {
            childrenByMethodId = new HashMap<>();
            firstUpdate = true;
        }

        public void update(ProfileNode profileNode)
        {
            setValue(profileNode);

            if (firstUpdate && getParent().isExpanded())
            {
                firstUpdate = false;
                setExpanded(profileNode.getTotalTimeShare() >= TIMESHARE_EXPAND_FACTOR);
            }

            ObservableList<TreeItem<ProfileNode>> children = getChildren();

            profileNode.getChildren().forEach(child ->
            {
                long methodId = child.getFrameInfo().getMethodId();

                MethodNodeAdapter childAdapter = childrenByMethodId
                    .computeIfAbsent(methodId, id ->
                    {
                        MethodNodeAdapter adapter = new MethodNodeAdapter();
                        children.add(adapter);
                        return adapter;
                    });

                childAdapter.update(child);
            });
        }

        public int getDepth()
        {
            return getValue() == null ? 0 : getValue().getDescendantDepth();
        }
    }
}
