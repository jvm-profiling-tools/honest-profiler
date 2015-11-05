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
package com.insightfullogic.honest_profiler.ports.javafx;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static com.insightfullogic.honest_profiler.ports.javafx.WindowViewModel.Window.Landing;

public class WindowViewModel
{

    public enum Window
    {
        Landing,
        Profile;

        private String getFxmlFile()
        {
            return name() + "View.fxml";
        }
    }

    private final PicoFXLoader loader;
    private final Stage stage;

    public WindowViewModel(PicoFXLoader loader, Stage stage)
    {
        this.loader = loader;
        this.stage = stage;
    }

    public Parent display(Window window)
    {
        Parent parent = loader.load(window.getFxmlFile());
        stage.setScene(new Scene(parent));
        stage.setMinWidth(650);
        stage.setMinHeight(400);
        return parent;
    }

    public Parent displayStart()
    {
        return display(Landing);
    }

}
