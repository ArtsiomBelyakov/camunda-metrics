package org.camunda.bpm.run.plugin.monitoring;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.config.MeterFilterReply;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

class MetricsConfigTest {

    private AnnotationConfigApplicationContext context;

    @BeforeEach
    void setUp() {
        context = new AnnotationConfigApplicationContext(MetricsConfig.class);
        MeterRegistry registry = new SimpleMeterRegistry();
        Metrics.globalRegistry.add(registry);
    }

    @Test
    void testExcludeAllExceptEngineRest() {
        // Get the filter from the context
        MeterFilter filter = context.getBean(MeterFilter.class);

        // Test case 1: Should deny non-http.server.requests metrics
        Meter.Id nonHttpMetric = new Meter.Id("other.metric", Tags.empty(), null, null, Meter.Type.COUNTER);
        assertEquals(MeterFilterReply.NEUTRAL, filter.accept(nonHttpMetric));

        // Test case 2: Should deny http.server.requests metrics without engine-rest URI
        Meter.Id nonEngineRestMetric = new Meter.Id("http.server.requests", 
            Tags.of("uri", "/other/path"), null, null, Meter.Type.COUNTER);
        assertEquals(MeterFilterReply.DENY, filter.accept(nonEngineRestMetric));

        // Test case 3: Should accept http.server.requests metrics with engine-rest URI
        Meter.Id engineRestMetric = new Meter.Id("http.server.requests", 
            Tags.of("uri", "/engine-rest/process-instance"), null, null, Meter.Type.COUNTER);
        assertEquals(MeterFilterReply.NEUTRAL, filter.accept(engineRestMetric));
    }

    @Test
    void testFilterWithMultipleTags() {
        MeterFilter filter = context.getBean(MeterFilter.class);

        // Test with multiple tags
        Meter.Id metricWithMultipleTags = new Meter.Id("http.server.requests", 
            Tags.of("uri", "/other/path", "method", "GET", "status", "200"), 
            null, null, Meter.Type.COUNTER);
        assertEquals(MeterFilterReply.DENY, filter.accept(metricWithMultipleTags));

        // Test with engine-rest URI and multiple tags
        Meter.Id engineRestMetricWithTags = new Meter.Id("http.server.requests", 
            Tags.of("uri", "/engine-rest/process-instance", "method", "GET", "status", "200"), 
            null, null, Meter.Type.COUNTER);
        assertEquals(MeterFilterReply.NEUTRAL, filter.accept(engineRestMetricWithTags));
    }
}
