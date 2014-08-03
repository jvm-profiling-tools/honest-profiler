package com.insightfullogic.honest_profiler.adapters;

import org.picocontainer.PicoContainer;
import org.picocontainer.injectors.FactoryInjector;
import org.picocontainer.injectors.InjectInto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

public class LoggerInjector extends FactoryInjector<Logger> {
    @Override
    public Logger getComponentInstance(PicoContainer container, Type into) {
        Class<?> cls = getClass(into);
        if(cls == null) {
            cls = ((InjectInto) into).getIntoClass();
        }
        return LoggerFactory.getLogger(cls);
    }
}
