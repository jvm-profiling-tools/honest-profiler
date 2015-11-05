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

import com.insightfullogic.honest_profiler.core.collector.FlatProfileEntry;
import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.core.profiles.ProfileListener;
import com.insightfullogic.honest_profiler.ports.javafx.GraphicalShareTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.MethodNameTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.Rendering;
import com.insightfullogic.honest_profiler.ports.javafx.TimeShareTableCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class FlatViewModel implements ProfileListener
{

    private final ObservableList<FlatProfileEntry> flatProfile;

    @FXML
    private TableView<FlatProfileEntry> flatProfileView;

    @FXML
    private TableColumn<FlatProfileEntry, String> methods;

    @FXML
    private TableColumn<FlatProfileEntry, Double> selfTimeGraphical;

    @FXML
    private TableColumn<FlatProfileEntry, Double> selfTimeShare;

    @FXML
    private TableColumn<FlatProfileEntry, Double> totalTimeShare;

    @FXML
    private void initialize()
    {
        methods.setCellValueFactory(Rendering::method);
        methods.setCellFactory(col -> new MethodNameTableCell());

        selfTimeGraphical.setCellValueFactory(new PropertyValueFactory<>("selfTimeShare"));
        selfTimeGraphical.setCellFactory(col -> new GraphicalShareTableCell(col.getPrefWidth()));

        configureTimeShareColumn(selfTimeShare, "selfTimeShare");
        configureTimeShareColumn(totalTimeShare, "totalTimeShare");

        flatProfileView.setItems(flatProfile);
    }

    private void configureTimeShareColumn(TableColumn<FlatProfileEntry, Double> column, String propertyName)
    {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new TimeShareTableCell());
    }

    public FlatViewModel()
    {
        flatProfile = FXCollections.observableArrayList();
    }

    @Override
    public void accept(Profile profile)
    {
        flatProfile.clear();
        profile.flatByMethodProfile()
            .forEach(flatProfile::add);
    }

}
