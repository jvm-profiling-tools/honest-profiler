package com.insightfullogic.honest_profiler.framework.generator;

import static com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext.ProfileMode.LOG;

import java.io.File;

import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfileListener;
import com.insightfullogic.honest_profiler.framework.LeanLogCollectorDriver;
import com.insightfullogic.honest_profiler.framework.scenario.LogScenario;
import com.insightfullogic.honest_profiler.ports.javafx.JavaFXApplication;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext.ProfileMode;

public class ProfileContextGenerator extends LeanLogCollectorDriver
{
    private JavaFXApplication application;
    private ProfileContext context;
    private LeanProfileListener wrappedListener;

    public ProfileContextGenerator(JavaFXApplication application, String name, ProfileMode mode)
    {
        super();

        this.application = application;

        reset();

        context = application.getContextForFile(new File(name), LOG);
        wrappedListener = context.getProfileListener();
    }

    public ProfileContext getProfileContext()
    {
        return context;
    }

    public void createNewProfile(LogScenario scenario)
    {
        scenario.executeAndEnd(this);
        application.testNewProfile(getProfileContext());
    }

    @Override
    public void accept(LeanProfile newProfile)
    {
        wrappedListener.accept(newProfile);
    }
}
