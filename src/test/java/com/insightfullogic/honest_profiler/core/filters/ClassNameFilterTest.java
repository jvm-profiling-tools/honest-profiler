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
package com.insightfullogic.honest_profiler.core.filters;

import com.insightfullogic.honest_profiler.core.collector.FlatProfileEntry;
import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.core.parser.Method;
import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import org.junit.runner.RunWith;

import static com.insightfullogic.honest_profiler.core.Util.list;
import static com.insightfullogic.lambdabehave.Suite.describe;

@RunWith(JunitSuiteRunner.class)
public class ClassNameFilterTest
{
    {

        describe("the class name filter", it -> {

        /*ProfileNode node = new ProfileNode(foo, 1.0);
        ProfileTree tree = new ProfileTree(node, 100);*/

            Method foo = new Method(1, "Foo.java", "Lfoo/Foo", "foo");
            Method bar = new Method(2, "Bar.java", "Lfoo/Bar", "foo");
            Method baz = new Method(3, "Baz.java", "Lfoo/Baz", "foo");

            ClassNameFilter filter = new ClassNameFilter("foo.B");

            it.should("remove methods covered by the filter from the flat profile", expect -> {
                Profile profile = new Profile(100, list(new FlatProfileEntry(foo, 1, 1, 1)), list(), list());
                filter.filter(profile);

                expect.that(profile.getFlatByMethodProfile()).isEmpty();
            });

            it.should("not remove methods retained by the filter from the flat profile", expect -> {
                Profile profile = new Profile(100, list(
                    new FlatProfileEntry(foo, 1, 1, 1),
                    new FlatProfileEntry(bar, 1, 1, 1),
                    new FlatProfileEntry(baz, 1, 1, 1)
                ), list(), list());
                filter.filter(profile);

                expect.that(profile.getFlatByMethodProfile()).containsInAnyOrder(
                    new FlatProfileEntry(bar, 1, 1, 1),
                    new FlatProfileEntry(baz, 1, 1, 1)
                );
            });

        });

    }}
