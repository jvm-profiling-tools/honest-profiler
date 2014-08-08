package com.insightfullogic.honest_profiler.delivery.javafx.profile;

import com.insightfullogic.honest_profiler.core.filters.FilterParseException;
import com.insightfullogic.honest_profiler.core.filters.ProfileFilter;
import com.insightfullogic.honest_profiler.delivery.javafx.WindowViewModel;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import static com.insightfullogic.honest_profiler.delivery.javafx.WindowViewModel.Window.Landing;

public class ProfileViewModel {

    private final WindowViewModel windows;
    private final ProfileFilter profileFilter;
    private final CachingProfileListener profileListener;

    private boolean flatView;

    @FXML
    private StackPane content;

    @FXML
    private TextField filterView;

    public ProfileViewModel(WindowViewModel windows, ProfileFilter profileFilter, CachingProfileListener profileListener) {
        this.windows = windows;
        this.profileFilter = profileFilter;
        this.profileListener = profileListener;

        flatView = false;
    }

    public void quit(ActionEvent event) {
        Platform.exit();
    }

    public void flipView(ActionEvent event) {
        Button button = (Button) event.getSource();
        flipButtonText(button);
        flipContent();
    }

    private void flipContent() {
        // StackPane only displays the head of its children list
        ObservableList<Node> children = content.getChildren();
        Node previouslyVisible = children.remove(0);
        children.add(previouslyVisible);
    }

    private void flipButtonText(Button button) {
        flatView = !flatView;
        button.setText(flatView ? "Tree View" : "Flat View");
    }

    public void back(ActionEvent actionEvent) {
        windows.display(Landing);
    }

    public void updateFilter(ActionEvent actionEvent) {
        try {
            profileFilter.updateFilters(filterView.getText());
        } catch (FilterParseException e) {
            e.printStackTrace();
        }
        profileListener.reflushLastProfile();
    }

}
