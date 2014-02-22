package com.insightfullogic.honest_profiler.javafx;

import com.insightfullogic.honest_profiler.collector.LogCollector;
import com.insightfullogic.honest_profiler.collector.Profile;
import com.insightfullogic.honest_profiler.javafx.flat.FlatViewModel;
import com.insightfullogic.honest_profiler.javafx.tree.TreeViewModel;
import com.insightfullogic.honest_profiler.log.LogParser;
import com.insightfullogic.honest_profiler.util.Listeners;
import javafx.scene.Parent;

import java.io.IOException;

public class ProfileWindow {

    public Parent initialise() {
        FlatViewModel flatModel = new FlatViewModel();
        TreeViewModel treeModel = new TreeViewModel();

        Listeners<Profile> listener = new Listeners<Profile>()
                .of(flatModel::accept)
                .of(treeModel::accept);

        LogCollector collector = new LogCollector(listener::accept);
        LogParser parser = new LogParser(collector);

        Loader loader = new Loader("ProfileView.fxml");
        Parent root = loader.getView();

        loader.getController(ProfileController.class)
                .setFlatModel(flatModel)
                .setTreeModel(treeModel)
                .setFileParser(parser::parse);

        return root;
    }

}
