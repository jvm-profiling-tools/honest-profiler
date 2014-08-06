package com.insightfullogic.honest_profiler.core.filters;

import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import org.junit.runner.RunWith;

import java.util.List;

import static com.insightfullogic.lambdabehave.Suite.describe;

@RunWith(JunitSuiteRunner.class)
public class FilterParserTest {{

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
            expect.exception(ParseException.class, () -> {
                Filters.parse("self time > 1.543;");
            });
        });

        it.should("throw an exception when the input is nonsense", expect -> {
            expect.exception(ParseException.class, () -> {
                Filters.parse("self dasdasds");
            });
        });

    });

}}
