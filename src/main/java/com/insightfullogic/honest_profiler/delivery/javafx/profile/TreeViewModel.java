package com.insightfullogic.honest_profiler.delivery.javafx.profile;

import com.insightfullogic.honest_profiler.core.ProfileListener;
import com.insightfullogic.honest_profiler.core.collector.Profile;
import com.insightfullogic.honest_profiler.core.collector.ProfileNode;
import com.insightfullogic.honest_profiler.core.collector.ProfileTree;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeViewModel implements ProfileListener {

    private static final double TIMESHARE_EXPAND_FACTOR = 0.2;

    @FXML
    private TreeView<ProfileNode> treeView;

    private final RootNodeAdapter rootNode = new RootNodeAdapter();

    @FXML
    private void initialize() {
        treeView.setCellFactory(view -> new TreeViewCell());
        treeView.setShowRoot(false);
        treeView.setRoot(rootNode);
    }

    @Override
    public void accept(Profile profile) {
        rootNode.update(profile.getTrees());
    }

    static class RootNodeAdapter extends TreeItem<ProfileNode> {

        private final Map<Long, ThreadNodeAdapter> threadsById;

        public RootNodeAdapter() {
            threadsById = new HashMap<>();
            setExpanded(true);
        }

        public void update(List<ProfileTree> trees) {
            ObservableList<TreeItem<ProfileNode>> children = getChildren();
            for (ProfileTree tree : trees) {
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

    static class ThreadNodeAdapter extends TreeItem<ProfileNode> {

        private final long threadId;
        private final MethodNodeAdapter adapter;

        public ThreadNodeAdapter(long threadId) {
            this.threadId = threadId;
            adapter = new MethodNodeAdapter();
            setExpanded(true);
            getChildren().add(adapter);
        }

        public void update(ProfileTree tree) {
            adapter.update(tree.getRootNode());
        }

        public long getThreadId() {
            return threadId;
        }
    }

    static class MethodNodeAdapter extends TreeItem<ProfileNode> {

        private final Map<Long, MethodNodeAdapter> childrenByMethodId;

        private boolean firstUpdate;

        public MethodNodeAdapter() {
            childrenByMethodId = new HashMap<>();
            firstUpdate = true;
        }

        public void update(ProfileNode profileNode) {
            setValue(profileNode);
            if (firstUpdate && getParent().isExpanded()) {
                firstUpdate = false;
                double timeShare = profileNode.getTotalTimeShare();
                setExpanded(timeShare >= TIMESHARE_EXPAND_FACTOR);
            }

            ObservableList<TreeItem<ProfileNode>> children = getChildren();
            profileNode.getChildren().forEach(child -> {
                long methodId = child.getMethod().getMethodId();
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
