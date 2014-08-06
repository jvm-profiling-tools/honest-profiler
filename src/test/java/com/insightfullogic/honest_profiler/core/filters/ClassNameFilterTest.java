package com.insightfullogic.honest_profiler.core.filters;

import com.insightfullogic.honest_profiler.core.collector.FlatProfileEntry;
import com.insightfullogic.honest_profiler.core.collector.Profile;
import com.insightfullogic.honest_profiler.core.parser.Method;
import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import org.junit.runner.RunWith;

import static com.insightfullogic.honest_profiler.core.Util.list;
import static com.insightfullogic.lambdabehave.Suite.describe;

@RunWith(JunitSuiteRunner.class)
public class ClassNameFilterTest {{

    describe("the class name filter", it -> {

        /*ProfileNode node = new ProfileNode(foo, 1.0);
        ProfileTree tree = new ProfileTree(node, 100);*/

        Method foo = new Method(1, "Foo.java", "Lfoo/Foo", "foo");
        Method bar = new Method(2, "Bar.java", "Lfoo/Bar", "foo");
        Method baz = new Method(3, "Baz.java", "Lfoo/Baz", "foo");

        ClassNameFilter filter = new ClassNameFilter("foo.B");

        it.should("remove methods covered by the filter from the flat profile", expect -> {
            Profile profile = new Profile(100, list(new FlatProfileEntry(foo, 1.0, 1.0)), list());
            filter.filter(profile);

            expect.that(profile.getFlatProfile()).isEmpty();
        });

        it.should("not remove methods retained by the filter from the flat profile", expect -> {
            Profile profile = new Profile(100, list(
                    new FlatProfileEntry(foo, 1.0, 1.0),
                    new FlatProfileEntry(bar, 1.0, 1.0),
                    new FlatProfileEntry(baz, 1.0, 1.0)
            ), list());
            filter.filter(profile);

            expect.that(profile.getFlatProfile()).containsInAnyOrder(
                    new FlatProfileEntry(bar, 1.0, 1.0),
                    new FlatProfileEntry(baz, 1.0, 1.0)
            );
        });

    });

}}
