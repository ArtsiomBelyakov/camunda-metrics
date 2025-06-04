package org.camunda.bpm.run.plugin.monitoring;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MetersTest {

    @Test
    void testMeterNames() {
        // Test some key meter names
        assertEquals("camunda.process.instances.started", Meters.PROCESS_INSTANCES_STARTED.getMeterName());
        assertEquals("camunda.incidents.created", Meters.INCIDENTS_CREATED.getMeterName());
        assertEquals("camunda.tasks.created", Meters.TASKS_CREATED.getMeterName());
        assertEquals("camunda.external.tasks.started", Meters.EXTERNAL_TASKS_STARTED.getMeterName());
    }

    @Test
    void testEventSubscriptionMeters() {
        // Test event subscription meter names
        assertEquals("camunda.message.event.subscriptions.active", Meters.MESSAGE.getMeterName());
        assertEquals("camunda.signal.event.subscriptions.active", Meters.SIGNAL.getMeterName());
        assertEquals("camunda.compensate.event.subscriptions.active", Meters.COMPENSATE.getMeterName());
        assertEquals("camunda.conditional.event.subscriptions.active", Meters.CONDITIONAL.getMeterName());
    }

    @Test
    void testAllMetersHaveValidNames() {
        // Verify that all meters have non-null and non-empty names
        for (Meters meter : Meters.values()) {
            assertNotNull(meter.getMeterName(), "Meter name should not be null");
            assertFalse(meter.getMeterName().isEmpty(), "Meter name should not be empty");
            assertTrue(meter.getMeterName().startsWith("camunda."), "Meter name should start with 'camunda.'");
        }
    }
} 