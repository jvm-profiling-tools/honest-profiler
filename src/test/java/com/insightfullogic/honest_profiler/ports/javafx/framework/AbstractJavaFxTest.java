package com.insightfullogic.honest_profiler.ports.javafx.framework;

import static com.insightfullogic.honest_profiler.ports.javafx.framework.HPFXUtil.isHeadless;
import static java.lang.System.gc;
import static org.junit.Assert.assertNotNull;
import static org.testfx.api.FxToolkit.registerPrimaryStage;
import static org.testfx.api.FxToolkit.setupApplication;
import static org.testfx.api.FxToolkit.setupStage;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.insightfullogic.honest_profiler.ports.javafx.JavaFXApplication;

import javafx.stage.Stage;

public abstract class AbstractJavaFxTest
{
    // Class properties

    private static Stage mainStage;

    // Class Accessors

    public static Stage getMainStage()
    {
        return mainStage;
    }

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
        if (isHeadless())
        {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
        }

        registerPrimaryStage();
        setupStage(stage ->
        {
            mainStage = stage;
            stage.show();
        });
    }

    @AfterClass
    public static void teardownSpec() throws Exception
    {
        mainStage = null;
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
        app = null;
        gc();
    }
}
