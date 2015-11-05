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

import com.insightfullogic.honest_profiler.core.Monitor;
import com.insightfullogic.honest_profiler.core.filters.ProfileFilter;
import com.insightfullogic.honest_profiler.ports.LoggerInjector;
import com.insightfullogic.honest_profiler.ports.javafx.landing.LandingViewModel;
import com.insightfullogic.honest_profiler.ports.javafx.profile.*;
import com.insightfullogic.honest_profiler.ports.sources.LocalMachineSource;
import com.insightfullogic.honest_profiler.ports.web.store.FileLogRepository;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;

public class JavaFXApplication extends Application
{

    private MutablePicoContainer pico;

    @Override
    public void start(Stage stage) throws Exception
    {
        stage.setTitle("Honest Profiler");
        stage.setWidth(1280);
        stage.setHeight(720);

        createStart(stage);
        stage.show();
    }

    @Override
    public void stop() throws Exception
    {
        pico.stop();
    }

    void createStart(Stage stage)
    {
        pico = registerComponents(stage);
        WindowViewModel stageModel = pico.getComponent(WindowViewModel.class);
        Parent parent = stageModel.displayStart();
        pico.start();
    }

    private static MutablePicoContainer registerComponents(Stage stage)
    {
        return registerComponents().addComponent(stage);
    }

    public static MutablePicoContainer registerComponents()
    {
        MutablePicoContainer pico = new PicoBuilder()
            .withJavaEE5Lifecycle()
            .withCaching()
            .build()

            .addAdapter(new LoggerInjector())
            .addAdapter(new ProfileListenerProvider())

                // Infrastructure
            .addComponent(LocalMachineSource.class)

                // Core
            .addComponent(FileLogRepository.class)
            .addComponent(Monitor.class)
            .addComponent(ProfileFilter.class)

                // Delivery
            .addComponent(CachingProfileListener.class)
            .addComponent(FlatViewModel.class)
            .addComponent(TreeViewModel.class)
            .addComponent(FlameGraphViewModel.class)
            .addComponent(TraceCountViewModel.class)
            .addComponent(ProfileViewModel.class)
            .addComponent(LandingViewModel.class)
            .addComponent(WindowViewModel.class)
            .addComponent(PicoFXLoader.class);

        return pico.addComponent(pico);
    }

    public static void main(String[] args)
    {
        launch(args);
    }

}
