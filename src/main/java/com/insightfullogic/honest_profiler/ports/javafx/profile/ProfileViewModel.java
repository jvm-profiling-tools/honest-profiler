/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
package com.insightfullogic.honest_profiler.ports.javafx.profile;

import com.insightfullogic.honest_profiler.core.filters.FilterParseException;
import com.insightfullogic.honest_profiler.core.filters.ProfileFilter;
import com.insightfullogic.honest_profiler.ports.javafx.WindowViewModel;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import static com.insightfullogic.honest_profiler.ports.javafx.WindowViewModel.Window.Landing;

public class ProfileViewModel
{

    private final WindowViewModel windows;
    private final ProfileFilter profileFilter;
    private final CachingProfileListener profileListener;

    private boolean flatView;

    @FXML
    private StackPane content;

    @FXML
    private TextField filterView;

    public ProfileViewModel(WindowViewModel windows,
                            ProfileFilter profileFilter,
                            CachingProfileListener profileListener)
    {
        this.windows = windows;
        this.profileFilter = profileFilter;
        this.profileListener = profileListener;

        flatView = true;
    }

    public void quit(ActionEvent event)
    {
        Platform.exit();
    }

    public void flipView(ActionEvent event)
    {
        Button button = (Button) event.getSource();
        flipButtonText(button);
        flipContent();
    }

    private void flipContent()
    {
        // StackPane only displays the head of its children list
        ObservableList<Node> children = content.getChildren();
        Node previouslyVisible = children.remove(0);
        children.add(previouslyVisible);
    }

    private void flipButtonText(Button button)
    {
        flatView = !flatView;
        button.setText(flatView ? "Tree View" : "Flat View");
    }

    public void back(ActionEvent actionEvent)
    {
        windows.display(Landing);
    }

    public void updateFilter(ActionEvent actionEvent)
    {
        try
        {
            profileFilter.updateFilters(filterView.getText());
        }
        catch (FilterParseException e)
        {
            e.printStackTrace();
        }
        profileListener.reflushLastProfile();
    }

}
