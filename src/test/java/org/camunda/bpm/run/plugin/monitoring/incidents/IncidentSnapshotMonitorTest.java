package org.camunda.bpm.run.plugin.monitoring.incidents;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.Incident;
import org.camunda.bpm.engine.runtime.IncidentQuery;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.run.plugin.monitoring.Meters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:camunda.cfg.xml"})
class IncidentSnapshotMonitorTest {

    @Autowired
    private ProcessEngine processEngine;
    
    private MeterRegistry meterRegistry;
    private IncidentSnapshotMonitor monitor;
    private RuntimeService runtimeService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        monitor = new IncidentSnapshotMonitor(processEngine, meterRegistry);
        runtimeService = processEngine.getRuntimeService();
    }

    @Test
    void testGetGaugeNames() {
        // Act
        List<String> gaugeNames = monitor.getGaugeNames();

        // Assert
        assertEquals(1, gaugeNames.size());
        assertTrue(gaugeNames.contains(Meters.INCIDENTS_OPEN.getMeterName()));
    }

    @Test
    @Deployment(resources = "processes/incident-process.bpmn")
    void testRetrieveGaugesDataWithRegularIncident() {
        // Arrange
        try {
            runtimeService.startProcessInstanceByKey("incidentProcess");
        } catch (Exception e) {
            // Expected exception that creates an incident
        }

        IncidentQuery query = runtimeService.createIncidentQuery();
        Incident incident = query.singleResult();
        assertNotNull(incident);
        assertEquals("failedActivity", incident.getActivityId());

        // Act
        var gaugesData = monitor.retrieveGaugesData();

        // Assert
        assertNotNull(gaugesData);
        assertEquals(1, gaugesData.size());
        
        var data = gaugesData.iterator().next();
        assertEquals(1L, data.getGaugesValues().get(Meters.INCIDENTS_OPEN.getMeterName()));
    }

    @Test
    @Deployment(resources = "processes/external-task.bpmn")
    void testRetrieveGaugesDataWithExternalTaskIncident() {
        // Arrange
        try {
            runtimeService.startProcessInstanceByKey("externalTaskProcess");
        } catch (Exception e) {
            // Expected exception that creates an incident
        }

        IncidentQuery query = runtimeService.createIncidentQuery();
        Incident incident = query.singleResult();
        assertNotNull(incident);
        assertEquals("externalTask", incident.getActivityId());

        // Act
        var gaugesData = monitor.retrieveGaugesData();

        // Assert
        assertNotNull(gaugesData);
        assertEquals(1, gaugesData.size());
        
        var data = gaugesData.iterator().next();
        assertEquals(1L, data.getGaugesValues().get(Meters.INCIDENTS_OPEN.getMeterName()));
    }
} 