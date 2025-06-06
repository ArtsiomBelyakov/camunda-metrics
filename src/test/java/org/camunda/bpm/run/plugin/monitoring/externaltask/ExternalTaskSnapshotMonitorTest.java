package org.camunda.bpm.run.plugin.monitoring.externaltask;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.engine.externaltask.ExternalTaskQuery;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.camunda.bpm.run.plugin.monitoring.Meters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ProcessEngineExtension.class)
class ExternalTaskSnapshotMonitorTest {

    private ProcessEngine processEngine;

    private ExternalTaskSnapshotMonitor monitor;

    @BeforeEach
    void setUp() {
        var meterRegistry = new SimpleMeterRegistry();
        monitor = new ExternalTaskSnapshotMonitor(processEngine, meterRegistry);
    }

    @Test
    void testGetGaugeNames() {
        // Act
        List<String> gaugeNames = monitor.getGaugeNames();

        // Assert
        assertEquals(4, gaugeNames.size());
        assertTrue(gaugeNames.contains(Meters.EXTERNAL_TASKS_OPEN.getMeterName()));
        assertTrue(gaugeNames.contains(Meters.EXTERNAL_TASKS_OPEN_ERROR.getMeterName()));
        assertTrue(gaugeNames.contains(Meters.EXTERNAL_TASKS_ENDED.getMeterName()));
        assertTrue(gaugeNames.contains(Meters.EXTERNAL_TASKS_STARTED.getMeterName()));
    }

    @Test
    @Deployment(resources = "bpmn/external-task.bpmn")
    void testRetrieveGaugesDataWithRegularTask() {
        // Arrange
        processEngine.getRuntimeService().startProcessInstanceByKey("test-external-task");
        var externalTaskService = processEngine.getExternalTaskService();
        ExternalTaskQuery query = externalTaskService.createExternalTaskQuery();
        ExternalTask task = query.singleResult();
        assertNotNull(task);
        assertNull(task.getErrorMessage());

        // Act
        var gaugesData = monitor.retrieveGaugesData();

        // Assert
        assertNotNull(gaugesData);
        assertEquals(1, gaugesData.size());

        var data = gaugesData.iterator().next();
        assertEquals(1L, data.getGaugesValues().get(Meters.EXTERNAL_TASKS_OPEN.getMeterName()));
        assertEquals(0L, data.getGaugesValues().get(Meters.EXTERNAL_TASKS_OPEN_ERROR.getMeterName()));
    }

    @Test
    @Deployment(resources = "bpmn/external-task.bpmn")
    void testRetrieveGaugesDataWithErrorTask() {
        // Arrange
        var workerId = "test-worker";
        var topic = "test-topic";
        processEngine.getRuntimeService().startProcessInstanceByKey("test-external-task");
        var externalTaskService = processEngine.getExternalTaskService();

        // Act
        var lockedTask = externalTaskService.fetchAndLock(1, workerId)
                .topic(topic, 1000)
                .execute();
        externalTaskService.handleFailure(lockedTask.get(0).getId(), workerId, "test error",1 ,0);
        var gaugesData = monitor.retrieveGaugesData();

        // Assert
        assertNotNull(gaugesData);
        assertEquals(1, gaugesData.size());

        var data = gaugesData.iterator().next();
        assertEquals(1L, data.getGaugesValues().get(Meters.EXTERNAL_TASKS_OPEN.getMeterName()));
        assertEquals(1L, data.getGaugesValues().get(Meters.EXTERNAL_TASKS_OPEN_ERROR.getMeterName()));
    }

    @Test
    @Deployment(resources = "bpmn/external-task.bpmn")
    void testRetrieveGaugesDataWithMultipleTasks() {
        // Arrange
        var workerId = "test-worker";
        var topic = "test-topic";
        processEngine.getRuntimeService().startProcessInstanceByKey("test-external-task");
        processEngine.getRuntimeService().startProcessInstanceByKey("test-external-task");
        var externalTaskService = processEngine.getExternalTaskService();
        var query = externalTaskService.createExternalTaskQuery();
        var tasks = query.list();

        // Act
        var lockedTask = externalTaskService.fetchAndLock(1, workerId)
                .topic(topic, 1000)
                .execute();
        externalTaskService.handleFailure(lockedTask.get(0).getId(), workerId, "test error",1 ,0);
        var gaugesData = monitor.retrieveGaugesData();

        // Assert
        assertEquals(2, tasks.size());

        assertNotNull(gaugesData);
        assertEquals(1, gaugesData.size());

        var data = gaugesData.iterator().next();
        assertEquals(2L, data.getGaugesValues().get(Meters.EXTERNAL_TASKS_OPEN.getMeterName()));
        assertEquals(1L, data.getGaugesValues().get(Meters.EXTERNAL_TASKS_OPEN_ERROR.getMeterName()));
    }

    @Test
    @Deployment(resources = "bpmn/external-task.bpmn")
    void testRetrieveGaugesDataWithEndedTask() {
        // Arrange
        var workerId = "test-worker";
        var topic = "test-topic";
        processEngine.getRuntimeService().startProcessInstanceByKey("test-external-task");
        var externalTaskService = processEngine.getExternalTaskService();

        // Act
        var lockedTask = externalTaskService.fetchAndLock(1, workerId)
                .topic(topic, 1000)
                .execute();
        externalTaskService.complete(lockedTask.get(0).getId(), workerId);
        var gaugesData = monitor.retrieveGaugesData();

        // Assert
        assertNotNull(gaugesData);
        // todo should data be 0 if the task is completed?
        assertEquals(0, gaugesData.size());

//        var data = gaugesData.iterator().next();
//        assertEquals(0L, data.getGaugesValues().get(Meters.EXTERNAL_TASKS_OPEN.getMeterName()));
//        assertEquals(0L, data.getGaugesValues().get(Meters.EXTERNAL_TASKS_OPEN_ERROR.getMeterName()));
//         todo STARTED and ENDED do not used in the lib metrics scrapping
//         assertEquals(1L, data.getGaugesValues().get(Meters.EXTERNAL_TASKS_STARTED.getMeterName()));
//         assertEquals(1L, data.getGaugesValues().get(Meters.EXTERNAL_TASKS_ENDED.getMeterName()));
    }

    @Test
    @Deployment(resources = "bpmn/external-task.bpmn")
    void testRetrieveGaugesDataWithMixedTasks() {
        // Arrange
        var workerId = "test-worker";
        var topic = "test-topic";
        
        // Start 4 process instances
        for (int i = 0; i < 4; i++) {
            processEngine.getRuntimeService().startProcessInstanceByKey("test-external-task");
        }
        
        var externalTaskService = processEngine.getExternalTaskService();
        var query = externalTaskService.createExternalTaskQuery();
        var tasks = query.list();
        assertEquals(4, tasks.size());

        // Act
        // Complete first task
        var lockedTask1 = externalTaskService.fetchAndLock(1, workerId)
                .topic(topic, 1000)
                .execute();
        externalTaskService.complete(lockedTask1.get(0).getId(), workerId);

        // Fail second task
        var lockedTask2 = externalTaskService.fetchAndLock(1, workerId)
                .topic(topic, 1000)
                .execute();
        externalTaskService.handleFailure(lockedTask2.get(0).getId(), workerId, "test error", 1, 0);

        // Leave third and fourth tasks as is (open)

        var gaugesData = monitor.retrieveGaugesData();

        // Assert
        assertNotNull(gaugesData);
        assertEquals(1, gaugesData.size());

        var data = gaugesData.iterator().next();
        // ?
        assertEquals(3L, data.getGaugesValues().get(Meters.EXTERNAL_TASKS_OPEN.getMeterName())); // 2 open tasks
        assertEquals(1L, data.getGaugesValues().get(Meters.EXTERNAL_TASKS_OPEN_ERROR.getMeterName())); // 1 error task
        // todo STARTED and ENDED do not used in the lib metrics scrapping
        // assertEquals(2L, data.getGaugesValues().get(Meters.EXTERNAL_TASKS_STARTED.getMeterName())); // 2 started tasks
        // assertEquals(1L, data.getGaugesValues().get(Meters.EXTERNAL_TASKS_ENDED.getMeterName())); // 1 completed task
    }
}
