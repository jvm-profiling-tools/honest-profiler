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

import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.core.profiles.ProfileListener;
import com.insightfullogic.honest_profiler.core.profiles.ProfileNode;
import com.insightfullogic.honest_profiler.core.profiles.ProfileTree;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeViewModel implements ProfileListener
{

    private static final double TIMESHARE_EXPAND_FACTOR = 0.2;

    @FXML
    private TreeView<ProfileNode> treeView;

    private final RootNodeAdapter rootNode = new RootNodeAdapter();

    @FXML
    private void initialize()
    {
        treeView.setCellFactory(view -> new TreeViewCell());
        treeView.setShowRoot(false);
        treeView.setRoot(rootNode);
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
                    ThreadNodeAdapter adapter = new ThreadNodeAdapter(id);
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
        private final MethodNodeAdapter adapter;

        public ThreadNodeAdapter(long threadId)
        {
            this.threadId = threadId;
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
