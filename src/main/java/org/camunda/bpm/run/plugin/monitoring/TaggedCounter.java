package org.camunda.bpm.run.plugin.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaggedCounter {

    private static final Logger log = LoggerFactory.getLogger(TaggedCounter.class);
    private static final int MAX_CACHE_SIZE = 1000; // Maximum number of unique tag combinations

    private final MeterRegistry registry;
    private final String name;
    private final Map<Tags, Counter> counters = new LinkedHashMap<Tags, Counter>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Tags, Counter> eldest) {
            if (size() > MAX_CACHE_SIZE) {
                log.warn(
                    "TaggedCounter cache exceeded max size {} for metric '{}'. Removing oldest entry with tags: {}. "
                        +
                        "This may indicate a cardinality issue with your metrics.",
                    MAX_CACHE_SIZE, name, eldest.getKey());
                // Remove counter from registry to prevent memory leak
                registry.remove(eldest.getValue().getId());
                return true;
            }
            return false;
        }
    };

    public TaggedCounter(String name, MeterRegistry registry) {
        this.name = name;
        this.registry = registry;
    }

    public void increment(Tags tags) {
        Counter counter = counters.computeIfAbsent(tags,
            k -> Counter.builder(name).tags(k).register(registry));
        counter.increment();
    }
}
