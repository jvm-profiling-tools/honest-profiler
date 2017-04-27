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
package com.insightfullogic.honest_profiler.ports.javafx;

import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.FXML_ROOT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.loaderFor;

import java.io.File;

import com.insightfullogic.honest_profiler.ports.javafx.controller.RootController;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext.ProfileMode;
import com.insightfullogic.honest_profiler.ports.javafx.util.FontUtil;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFXApplication extends Application
{
    private RootController rootController;

    @Override
    public void init()
    {
        FontUtil.initialize(getClass());
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        stage.setTitle("Honest Profiler");
        stage.setWidth(1280);
        stage.setHeight(720);

        FXMLLoader loader = loaderFor(this, FXML_ROOT);
        Parent root = (Parent)loader.load();
        rootController = loader.getController();

        stage.setScene(new Scene(root));
        stage.setMinWidth(650);
        stage.setMinHeight(400);

        stage.show();
    }

    @Override
    public void stop() throws Exception
    {
        super.stop();
        rootController.close();
    }

    public static void main(String[] args)
    {
        launch(args);
    }

    // For Unit Testing Only

    public ProfileContext getContextForFile(File file, ProfileMode mode)
    {
        return rootController.getContextForFile(file, mode);
    }

    public void testNewProfile(File file, boolean live)
    {
        rootController.testNewProfile(file, live);
    }

    public void testNewProfile(ProfileContext context)
    {
        rootController.testNewProfile(context);
    }
}
