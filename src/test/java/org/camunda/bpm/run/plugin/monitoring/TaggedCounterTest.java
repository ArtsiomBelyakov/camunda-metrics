package org.camunda.bpm.run.plugin.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaggedCounterTest {
    private MeterRegistry registry;
    private TaggedCounter taggedCounter;
    private static final String COUNTER_NAME = "test.counter";

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        taggedCounter = new TaggedCounter(COUNTER_NAME, registry);
    }

    @Test
    void testIncrementWithSingleTag() {
        // Arrange
        Tags tags = Tags.of("key", "value");

        // Act
        taggedCounter.increment(tags);

        // Assert
        Counter counter = registry.find(COUNTER_NAME).tags(tags).counter();
        assertNotNull(counter, "Counter should be created");
        assertEquals(1.0, counter.count(), "Counter should be incremented once");
    }

    @Test
    void testIncrementWithMultipleTags() {
        // Arrange
        Tags tags = Tags.of("key1", "value1", "key2", "value2");

        // Act
        taggedCounter.increment(tags);
        taggedCounter.increment(tags);

        // Assert
        Counter counter = registry.find(COUNTER_NAME).tags(tags).counter();
        assertNotNull(counter, "Counter should be created");
        assertEquals(2.0, counter.count(), "Counter should be incremented twice");
    }

    @Test
    void testIncrementWithDifferentTags() {
        // Arrange
        Tags tags1 = Tags.of("key", "value1");
        Tags tags2 = Tags.of("key", "value2");

        // Act
        taggedCounter.increment(tags1);
        taggedCounter.increment(tags2);

        // Assert
        Counter counter1 = registry.find(COUNTER_NAME).tags(tags1).counter();
        Counter counter2 = registry.find(COUNTER_NAME).tags(tags2).counter();
        
        assertNotNull(counter1, "First counter should be created");
        assertNotNull(counter2, "Second counter should be created");
        assertEquals(1.0, counter1.count(), "First counter should be incremented once");
        assertEquals(1.0, counter2.count(), "Second counter should be incremented once");
    }

    @Test
    void testCounterReuse() {
        // Arrange
        Tags tags = Tags.of("key", "value");

        // Act
        taggedCounter.increment(tags);
        taggedCounter.increment(tags);
        taggedCounter.increment(tags);

        // Assert
        Counter counter = registry.find(COUNTER_NAME).tags(tags).counter();
        assertNotNull(counter, "Counter should be created");
        assertEquals(3.0, counter.count(), "Counter should be incremented three times");
    }
}
