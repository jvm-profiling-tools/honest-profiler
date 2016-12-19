/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
package com.insightfullogic.honest_profiler.ports.javafx.profile;

import static com.insightfullogic.honest_profiler.ports.javafx.Rendering.renderMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.core.profiles.ProfileListener;
import com.insightfullogic.honest_profiler.core.profiles.ProfileNode;
import com.insightfullogic.honest_profiler.core.profiles.ProfileTree;
import com.insightfullogic.honest_profiler.ports.javafx.Rendering;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.util.Callback;

public class TreeTableViewModel implements ProfileListener
{
    private static final double TIMESHARE_EXPAND_FACTOR = 0.2;

    @FXML
    private TreeTableView<ProfileNode> treeView;

    private final RootNodeAdapter rootNode = new RootNodeAdapter();

    @FXML
    private void initialize()
    {
        treeView.setShowRoot(false);
        treeView.setRoot(rootNode);
        treeView.setEditable(false);
        
        TreeTableColumn<ProfileNode, String> totalColumn = new TreeTableColumn<>("Total");
        totalColumn.setSortable(false);
        totalColumn.setCellValueFactory(
            (TreeTableColumn.CellDataFeatures<ProfileNode, String> param) -> 
            new ReadOnlyStringWrapper(param.getValue().getValue()!=null?Rendering.renderTimeShare(param.getValue().getValue().getTotalTimeShare()):"")
        );

        TreeTableColumn<ProfileNode, String> selfColumn = new TreeTableColumn<>("Self");
        selfColumn.setSortable(false);
        selfColumn.setCellValueFactory(
            (TreeTableColumn.CellDataFeatures<ProfileNode, String> param) -> 
            new ReadOnlyStringWrapper(param.getValue().getValue()!=null?Rendering.renderTimeShare(param.getValue().getValue().getSelfTimeShare()):"")
        );
            
        TreeTableColumn<ProfileNode, String> methodColumn = new TreeTableColumn<>("Method");
        methodColumn.setSortable(false);
        methodColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<ProfileNode, String> param) -> {
                	TreeItem<ProfileNode> treeItem = param.getValue();
                	String text = "";
                    if (treeItem instanceof ThreadNodeAdapter)
                    {
                        ThreadNodeAdapter adapter = (ThreadNodeAdapter) treeItem;
                        String name = adapter.getThreadName();
                        StringBuilder builder = new StringBuilder("Thread").append(' ');
                        if (adapter.getThreadId() < 0) {
                            builder.append("unknown id");
                        } else {
                            builder.append(adapter.getThreadId());
                        }
                        builder
                            .append(' ')
                            .append('[')
                            .append((name == null || "".equals(name)) ? "Unknown" : name)
                            .append(']');
                        text = builder.toString();
                    }
                    else if (treeItem instanceof MethodNodeAdapter)
                    {
                    	text = renderMethod(treeItem.getValue().getFrameInfo());
                    }
				return new ReadOnlyStringWrapper(text);
                }
            );
        
        TreeTableColumn<ProfileNode, ProfileNode> percentColumn = new TreeTableColumn<>("%");
        percentColumn.setPrefWidth(65);
        percentColumn.setSortable(false);
        percentColumn.setResizable(false);
        percentColumn.setCellFactory(new Callback<TreeTableColumn<ProfileNode, ProfileNode>, TreeTableCell<ProfileNode, ProfileNode>>()
        {
            @Override
            public TreeTableCell<ProfileNode, ProfileNode> call(TreeTableColumn<ProfileNode, ProfileNode> param)
            {
            	return new TreeTableViewCell();
            }}
        );
        
        treeView.getColumns().setAll(methodColumn, percentColumn, totalColumn, selfColumn);
    }

    @Override
    public void accept(Profile profile)
    {
        rootNode.update(profile.getTrees());
    }

    static class RootNodeAdapter extends TreeItem<ProfileNode>
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
            for (ProfileTree tree : trees)
            {
                long threadId = tree.getThreadId();
                ThreadNodeAdapter thread = threadsById.computeIfAbsent(threadId, id -> {
                    ThreadNodeAdapter adapter = new ThreadNodeAdapter(id, tree.getThreadName());
                    children.add(adapter);
                    return adapter;
                });
                thread.update(tree);
            }
        }
    }

    static class ThreadNodeAdapter extends TreeItem<ProfileNode>
    {

        private final long threadId;
        private final String threadName;
        private final MethodNodeAdapter adapter;

        public ThreadNodeAdapter(long threadId, String threadName)
        {
            this.threadId = threadId;
            this.threadName = threadName;
            adapter = new MethodNodeAdapter();
            setExpanded(true);
            getChildren().add(adapter);
        }

        public void update(ProfileTree tree)
        {
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
    }

    static class MethodNodeAdapter extends TreeItem<ProfileNode>
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
                double timeShare = profileNode.getTotalTimeShare();
                setExpanded(timeShare >= TIMESHARE_EXPAND_FACTOR);
            }

            ObservableList<TreeItem<ProfileNode>> children = getChildren();
            profileNode.getChildren().forEach(child -> {
                long methodId = child.getFrameInfo().getMethodId();
                MethodNodeAdapter childAdapter = childrenByMethodId.computeIfAbsent(methodId, id -> {
                    MethodNodeAdapter adapter = new MethodNodeAdapter();
                    children.add(adapter);
                    return adapter;
                });
                childAdapter.update(child);
            });
        }
    }

}
