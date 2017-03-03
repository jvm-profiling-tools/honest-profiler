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

import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.FXML_ROOT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.loaderFor;
import static java.lang.System.currentTimeMillis;
import static javafx.scene.input.KeyCode.ENTER;
import static org.hamcrest.Matchers.is;
import static org.loadui.testfx.Assertions.assertNodeExists;
import static org.loadui.testfx.controls.TableViews.numberOfRowsIn;

import java.util.concurrent.Callable;

import org.hamcrest.Matcher;
import org.junit.Ignore;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.loadui.testfx.exceptions.NodeQueryException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class JavaFxApplicationTest extends GuiTest
{

    @Ignore
    @Test
    public void loadsLog() throws InterruptedException
    {
        // autocompletes
        click("Open").type("exa").type(ENTER);

        verifyFuture(() -> numberOfRowsIn("#flatProfileView"), is(38));
        await(() -> assertNodeExists("Example.subMethod"), 500);
    }

    private void await(Runnable assertion, long timeoutInMS)
    {
        long deadline = currentTimeMillis() + timeoutInMS;

        while (deadline > currentTimeMillis())
        {
            try
            {
                assertion.run();
                return;
            }
            catch (NodeQueryException e)
            {
                // deliberately blank
            }
            sleep(25);
        }
        assertion.run();
    }

    private <T> void verifyFuture(Callable<T> callable, Matcher<T> condition)
    {
        waitUntil(callable, condition);
    }

    @Override
    protected Parent getRootNode()
    {
        FXMLLoader loader = loaderFor(this, FXML_ROOT);
        return loader.getRoot();
    }
}
