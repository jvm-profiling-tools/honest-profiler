package com.insightfullogic.honest_profiler.javafx.profile;

import com.insightfullogic.honest_profiler.collector.ProfileListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProfileListenerProvider {

    @Bean
    public ProfileListener provide(FlatViewModel flatModel, TreeViewModel treeModel, TraceCountViewModel countModel) {
        return profile -> {
            flatModel.accept(profile);
            treeModel.accept(profile);
            countModel.accept(profile);
        };
    }

}
