package com.insightfullogic.honest_profiler.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Listeners<T> implements Consumer<T> {

    private final List<Consumer<T>> consumers;

    public Listeners() {
        consumers = new ArrayList<>();
    }

    public Listeners<T> of(Consumer<T> consumer) {
        consumers.add(consumer);
        return this;
    }

    @Override
    public void accept(T value) {
        consumers.forEach(consumer -> consumer.accept(value));
    }

}
