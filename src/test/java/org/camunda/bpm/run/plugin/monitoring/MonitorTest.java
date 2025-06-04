package org.camunda.bpm.run.plugin.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MultiGauge;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.repository.ProcessDefinitionQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MonitorTest {

    @Mock
    private ProcessEngine processEngine;
    
    private MeterRegistry meterRegistry;
    private TestMonitor monitor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        meterRegistry = new SimpleMeterRegistry();
        monitor = new TestMonitor(processEngine, meterRegistry);
    }

    @Test
    void testInitMultiGauges() {
        // Verify that MultiGauges are initialized for all gauge names
        Map<String, MultiGauge> multiGaugeMap = monitor.getMultiGaugeMap();
        assertNotNull(multiGaugeMap);
        assertEquals(2, multiGaugeMap.size());
        assertTrue(multiGaugeMap.containsKey("test.gauge.1"));
        assertTrue(multiGaugeMap.containsKey("test.gauge.2"));
    }

    @Test
    void testUpdate() {
        // Arrange
        monitor.update();

        // Assert
        MultiGauge gauge1 = monitor.getMultiGaugeMap().get("test.gauge.1");
        MultiGauge gauge2 = monitor.getMultiGaugeMap().get("test.gauge.2");
        
        assertNotNull(gauge1);
        assertNotNull(gauge2);
    }

    @Test
    void testGetTagsForProcessDefinition() {
        // Arrange
        String processDefinitionId = "test-process";
        ProcessDefinition processDefinition = mock(ProcessDefinition.class);
        ProcessDefinitionQuery query = mock(ProcessDefinitionQuery.class);
        
        when(processDefinition.getId()).thenReturn(processDefinitionId);
        when(processDefinition.getKey()).thenReturn("test-key");
        when(processDefinition.getTenantId()).thenReturn("test-tenant");
        when(processEngine.getRepositoryService().createProcessDefinitionQuery()).thenReturn(query);
        when(query.processDefinitionId(processDefinitionId)).thenReturn(query);
        when(query.singleResult()).thenReturn(processDefinition);

        // Act
        Tags tags = monitor.getTagsForProcessDefinition(processDefinitionId);

        // Assert
        assertNotNull(tags);
        Map<String, String> tagMap = tags.stream()
            .collect(Collectors.toMap(Tag::getKey, Tag::getValue));
        assertEquals("test-tenant", tagMap.get("tenant.id"));
        assertEquals(processDefinitionId, tagMap.get("process.definition.id"));
        assertEquals("test-key", tagMap.get("process.definition.key"));
    }

    // Test implementation of Monitor
    private static class TestMonitor extends Monitor {
        public TestMonitor(ProcessEngine processEngine, MeterRegistry meterRegistry) {
            super(processEngine, meterRegistry);
        }

        @Override
        protected List<String> getGaugeNames() {
            return Arrays.asList("test.gauge.1", "test.gauge.2");
        }

        @Override
        protected Collection<MultiGaugeData> retrieveGaugesData() {
            Map<String, Long> values = Map.of(
                "test.gauge.1", 10L,
                "test.gauge.2", 20L
            );
            return List.of(new MultiGaugeData(values, Tags.of("test", "value")));
        }

        public Map<String, MultiGauge> getMultiGaugeMap() {
            return super.multiGaugeMap;
        }
    }
} 