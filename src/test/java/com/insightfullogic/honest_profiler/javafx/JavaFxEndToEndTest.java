package com.insightfullogic.honest_profiler.javafx;

import javafx.scene.Parent;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.loadui.testfx.exceptions.NodeQueryException;

import java.util.concurrent.Callable;

import static java.lang.System.currentTimeMillis;
import static javafx.scene.input.KeyCode.ENTER;
import static org.hamcrest.Matchers.is;
import static org.loadui.testfx.Assertions.assertNodeExists;
import static org.loadui.testfx.controls.TableViews.numberOfRowsIn;

public class JavaFxEndToEndTest extends GuiTest {

    @Test
    public void loadsLog() throws InterruptedException {
        // autocompletes
        click("Open").type("exa").type(ENTER);

        verifyFuture(() -> numberOfRowsIn("#flatProfileView"), is(38));
        await(() -> assertNodeExists("Example.subMethod"), 500);
    }

    private void await(Runnable assertion, long timeoutInMS) {
        long deadline = currentTimeMillis() + timeoutInMS;

        while (deadline > currentTimeMillis()) {
            try {
                assertion.run();
                return;
            } catch (NodeQueryException e) {
                // deliberately blank
            }
            sleep(25);
        }
        assertion.run();
    }

    private <T> void verifyFuture(Callable<T> callable, Matcher<T> condition) {
        waitUntil(callable, condition);
    }

    @Override
    protected Parent getRootNode() {
        // TODO
        return null; //JavaFXEntry.createStart(stage);
    }

}
