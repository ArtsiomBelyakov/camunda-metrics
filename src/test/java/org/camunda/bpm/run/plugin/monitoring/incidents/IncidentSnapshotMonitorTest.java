package org.camunda.bpm.run.plugin.monitoring.incidents;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.Incident;
import org.camunda.bpm.engine.runtime.IncidentQuery;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.camunda.bpm.run.plugin.monitoring.Meters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ProcessEngineExtension.class)
class IncidentSnapshotMonitorTest {
    private ProcessEngine processEngine;

    private IncidentSnapshotMonitor monitor;
    private RuntimeService runtimeService;

    @BeforeEach
    void setUp() {
        var meterRegistry = new SimpleMeterRegistry();
        monitor = new IncidentSnapshotMonitor(processEngine, meterRegistry);
        runtimeService = processEngine.getRuntimeService();
    }

    @Test
    void testGetGaugeNames() {
        // Act
        List<String> gaugeNames = monitor.getGaugeNames();

        // Assert
        assertEquals(6, gaugeNames.size());
        assertTrue(gaugeNames.contains(Meters.INCIDENTS_OPEN.getMeterName()));
    }

    @Test
    @Deployment(resources = {
            "bpmn/test.incident.call_activity1.bpmn",
            "bpmn/test.incident.call_activity2.bpmn",
            "bpmn/test.incident.called.terminate.bpmn"
    })
    void testRetrieveGaugesDataWithRegularIncident() {
        // Arrange
        var workerId = "test-worker";
        var topicName = "test-topic";
        runtimeService.startProcessInstanceByKey("test.incident.call_activity2");
        var externalTaskService = processEngine.getExternalTaskService();

        // Act
        var lockedTasks = externalTaskService.fetchAndLock(1, workerId)
                .topic(topicName, 1000)
                .execute();
        externalTaskService.handleFailure(lockedTasks.get(0).getId(), workerId, "test error", 0, 0);
        var gaugesData = monitor.retrieveGaugesData();

        // Assert
        assertNotNull(gaugesData);
        assertEquals(1, gaugesData.size());

        var data = gaugesData.iterator().next();

        // Verify all expected tags are present with correct values
        var tags = data.getTags();
        assertTrue(tags.stream().anyMatch(tag -> tag.getKey().equals("topic.name") && tag.getValue().equals(topicName)),
                "Topic name tag should match");
        assertTrue(tags.stream().anyMatch(tag -> tag.getKey().equals("tenant.id") && tag.getValue().isEmpty()),
                "Tenant ID should be empty");
        assertTrue(tags.stream().anyMatch(tag -> tag.getKey().equals("process.definition.id")),
                "Process definition ID should be present");
        assertTrue(tags.stream().anyMatch(tag -> tag.getKey().equals("process.definition.key")
                && tag.getValue().equals("test.incident.call_activity1")),
                "Process definition key should be present");
        assertTrue(tags.stream().anyMatch(tag -> tag.getKey().equals("activity.id")
                        && tag.getValue().equals("external.task")),
                "Activity ID should be present");
        assertTrue(tags.stream().anyMatch(tag -> tag.getKey().equals("failed.activity.id")),
                "Failed activity ID should be present");
        assertTrue(tags.stream().anyMatch(tag -> tag.getKey().equals("incident.type")
                && tag.getValue().equals("failedExternalTask")),
                "Incident type should be present");

        // Verify gauge values
        assertEquals(1L, data.getGaugesValues().get(Meters.INCIDENTS_OPEN.getMeterName()));
    }

    @Test
    @Deployment(resources = {
            "bpmn/test.incident.call_activity1.bpmn",
            "bpmn/test.incident.call_activity2.bpmn",
            "bpmn/test.incident.called.terminate.bpmn"
    })
    void testRetrieveGaugesDataWithExternalTaskIncident() {
        // Arrange
        var workerId = "test-worker";
        var topicName = "test-topic";
        runtimeService.startProcessInstanceByKey("test.incident.call_activity2");
        var externalTaskService = processEngine.getExternalTaskService();

        // Act
        // First task in test.incident.call_activity1
        var firstTask = externalTaskService.fetchAndLock(1, workerId)
                .topic(topicName, 1000)
                .execute();
        
        // Complete first task successfully
        externalTaskService.complete(firstTask.get(0).getId(), workerId);
        
        // Second task in test.incident.called.terminate
        var secondTask = externalTaskService.fetchAndLock(1, workerId)
                .topic(topicName, 1000)
                .execute();
        
        // Fail second task
        externalTaskService.handleFailure(secondTask.get(0).getId(), workerId, "test error", 0, 0);
        
        var gaugesData = monitor.retrieveGaugesData();

        // Assert
        assertNotNull(gaugesData);
        assertEquals(1, gaugesData.size());

        var data = gaugesData.iterator().next();
        
        // Verify all expected tags are present with correct values
        var tags = data.getTags();
        assertTrue(tags.stream().anyMatch(tag -> tag.getKey().equals("topic.name") && tag.getValue().equals(topicName)),
                "Topic name tag should match");
        assertTrue(tags.stream().anyMatch(tag -> tag.getKey().equals("tenant.id") && tag.getValue().isEmpty()),
                "Tenant ID should be empty");
        assertTrue(tags.stream().anyMatch(tag -> tag.getKey().equals("process.definition.id")),
                "Process definition ID should be present");
        assertTrue(tags.stream().anyMatch(tag -> tag.getKey().equals("process.definition.key")
                && tag.getValue().equals("test.incident.called.terminate")),
                "Process definition key should be present");
        assertTrue(tags.stream().anyMatch(tag -> tag.getKey().equals("activity.id")
                && tag.getValue().equals("external.task")),
                "Activity ID should be present");
        assertTrue(tags.stream().anyMatch(tag -> tag.getKey().equals("failed.activity.id")),
                "Failed activity ID should be present");
        assertTrue(tags.stream().anyMatch(tag -> tag.getKey().equals("incident.type")
                && tag.getValue().equals("failedExternalTask")),
                "Incident type should be present");
        
        // Verify gauge values
        assertEquals(1L, data.getGaugesValues().get(Meters.INCIDENTS_OPEN.getMeterName()));
    }
}
