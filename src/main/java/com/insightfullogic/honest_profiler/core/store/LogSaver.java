package com.insightfullogic.honest_profiler.core.store;

import java.nio.ByteBuffer;

public interface LogSaver {

    void save(ByteBuffer data);

    void close();

}
