package com.insightfullogic.honest_profiler.core.filters;

import com.insightfullogic.honest_profiler.core.collector.Profile;
import com.insightfullogic.honest_profiler.core.collector.ProfileTree;
import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import org.junit.runner.RunWith;

import static com.insightfullogic.honest_profiler.core.Util.list;
import static com.insightfullogic.lambdabehave.Suite.describe;

@RunWith(JunitSuiteRunner.class)
public class ThreadSampleFilterTest {{

    describe("the thread sample filter", it -> {

        ThreadSampleFilter filter = new ThreadSampleFilter();

        it.should("not remove trees with as many samples as sampled in total", expect -> {
            ProfileTree tree = new ProfileTree(null, 100);
            Profile profile = new Profile(100, null, list(tree));
            filter.filter(profile);

            expect.that(profile.getTrees()).contains(tree);
        });

        it.should("remove trees with very few samples", expect -> {
            ProfileTree tree = new ProfileTree(null, 1);
            Profile profile = new Profile(150, null, list(tree));
            filter.filter(profile);

            expect.that(profile.getTrees()).isEmpty();
        });

    });

}}
