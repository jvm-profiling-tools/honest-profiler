package com.insightfullogic.honest_profiler.ports.javafx.framework;

import static org.junit.Assert.assertNotNull;
import static org.testfx.api.FxToolkit.registerPrimaryStage;
import static org.testfx.api.FxToolkit.setupApplication;
import static org.testfx.api.FxToolkit.setupStage;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import com.insightfullogic.honest_profiler.ports.javafx.JavaFXApplication;

public abstract class AbstractJavaFxTest
{
    // Instance Properties

    private JavaFXApplication app;

    // Instance Accessors

    public JavaFXApplication app()
    {
        return app;
    }

    // Setup and Teardown

    @BeforeClass
    public static void setupSpec() throws Exception
    {
        registerPrimaryStage();
        setupStage(stage -> stage.show());
    }

    @Before
    public void setup() throws Exception
    {
        app = (JavaFXApplication)setupApplication(JavaFXApplication.class);
        assertNotNull(app);
    }

    @After
    public void teardown() throws Exception
    {
        app.stop();
    }
}
