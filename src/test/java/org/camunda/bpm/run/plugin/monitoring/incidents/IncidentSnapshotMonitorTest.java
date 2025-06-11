package org.camunda.bpm.run.plugin.monitoring.incidents;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.run.plugin.monitoring.Meters.*;
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
        assertThat(gaugeNames).containsExactlyInAnyOrder(
                INCIDENTS_CREATED.getMeterName(),
                INCIDENTS_DELETED.getMeterName(),
                INCIDENTS_RESOLVED.getMeterName(),
                INCIDENTS_OPEN.getMeterName(),
                INCIDENTS_OPEN_NEWEST.getMeterName(),
                INCIDENTS_OPEN_OLDEST.getMeterName()
        );
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
        assertThat(tags.stream().map(Tag::getKey).toList())
                .containsExactlyInAnyOrder(
                        "topic.name",
                        "tenant.id",
                        "process.definition.id",
                        "process.definition.key",
                        "activity.id",
                        "failed.activity.id",
                        "incident.type"
                );
        assertThat(tags).allMatch(tag -> switch (tag.getKey()) {
            case "topic.name" -> tag.getValue().equals(topicName);
            case "tenant.id" -> tag.getValue().isEmpty();
            case "process.definition.id" -> !tag.getValue().isEmpty();
            case "process.definition.key" -> tag.getValue().equals("test.incident.call_activity1");
            case "activity.id" -> tag.getValue().equals("external.task");
            case "incident.type" -> tag.getValue().equals("failedExternalTask");
            default -> true;
        });

        // Verify gauge values
        assertEquals(1, data.getGaugesValues().get(Meters.INCIDENTS_OPEN.getMeterName()));
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
        assertThat(tags.stream().map(Tag::getKey).toList())
                .containsExactlyInAnyOrder(
                        "topic.name",
                        "tenant.id",
                        "process.definition.id",
                        "process.definition.key",
                        "activity.id",
                        "failed.activity.id",
                        "incident.type"
                );
        assertThat(tags).allMatch(tag -> switch (tag.getKey()) {
            case "topic.name" -> tag.getValue().equals(topicName);
            case "tenant.id" -> tag.getValue().isEmpty();
            case "process.definition.id" -> !tag.getValue().isEmpty();
            case "process.definition.key" -> tag.getValue().equals("test.incident.called.terminate");
            case "activity.id" -> tag.getValue().equals("internal.external.task");
            case "incident.type" -> tag.getValue().equals("failedExternalTask");
            default -> true;
        });

        // Verify gauge values
        assertEquals(1, data.getGaugesValues().get(Meters.INCIDENTS_OPEN.getMeterName()));
    }
}
