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

import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import org.junit.runner.RunWith;

import java.util.List;

import static com.insightfullogic.lambdabehave.Suite.describe;

@RunWith(JunitSuiteRunner.class)
public class FilterParserTest
{
    {

        describe("filter parsing", it -> {

            it.should("not remove anything for empty strings", expect -> {
                expect.that(Filters.parse("")).hasSize(1)
                    .contains(new ThreadSampleFilter());
            });

            it.should("parse total time filters", expect -> {
                List<Filter> filters = Filters.parse("total time > 0.3;");

                expect.that(filters).hasItem(new TotalTimeShareFilter(0.3));
            });

            it.should("parse self time filters", expect -> {
                List<Filter> filters = Filters.parse("self time > 0.543;");

                expect.that(filters).hasItem(new SelfTimeShareFilter(0.543));
            });

            it.should("parse class name filters", expect -> {
                List<Filter> filters = Filters.parse("class: foo.bar.Baz;");

                expect.that(filters).hasItem(new ClassNameFilter("foo.bar.Baz"));
            });

            it.should("parse multiple filters", expect -> {
                List<Filter> filters = Filters.parse("class: foo.bar.Baz;self time > 0.543;");

                expect.that(filters).containsInAnyOrder(
                    new ClassNameFilter("foo.bar.Baz"),
                    new SelfTimeShareFilter(0.543),
                    new ThreadSampleFilter()
                );
            });

            it.should("validate time filters", expect -> {
                expect.exception(FilterParseException.class, () -> {
                    Filters.parse("self time > 1.543;");
                });
            });

            it.should("throw an exception when the input is nonsense", expect -> {
                expect.exception(FilterParseException.class, () -> {
                    Filters.parse("self dasdasds");
                });
            });

        });

    }}
