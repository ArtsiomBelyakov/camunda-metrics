package org.camunda.bpm.run.plugin.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;

import java.util.HashMap;
import java.util.Map;

public class TaggedCounter {
    private final MeterRegistry registry;
    private final String name;
    private final Map<Tags, Counter> counters = new HashMap<>();

    public TaggedCounter(String name, MeterRegistry registry) {
        this.name = name;
        this.registry = registry;
    }

    public void increment(Tags tags) {
//        Counter counter = counters.get(tags);
//        if (counter == null) {
//            counter = Counter.builder(name).tags(tags).register(registry);
//            counters.put(tags, counter);
//        }
//        counter.increment();

        Counter counter = counters.computeIfAbsent(tags, k -> Counter.builder(name).tags(k).register(registry));
        counter.increment();
    }
}
