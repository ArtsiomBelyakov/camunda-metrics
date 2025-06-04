package org.camunda.bpm.run.plugin.monitoring.externaltask;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.engine.externaltask.ExternalTaskQuery;
import org.camunda.bpm.engine.externaltask.ExternalTaskService;
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
class ExternalTaskSnapshotMonitorTest {

    @Autowired
    private ProcessEngine processEngine;
    
    private MeterRegistry meterRegistry;
    private ExternalTaskSnapshotMonitor monitor;
    private ExternalTaskService externalTaskService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        monitor = new ExternalTaskSnapshotMonitor(processEngine, meterRegistry);
        externalTaskService = processEngine.getExternalTaskService();
    }

    @Test
    void testGetGaugeNames() {
        // Act
        List<String> gaugeNames = monitor.getGaugeNames();

        // Assert
        assertEquals(2, gaugeNames.size());
        assertTrue(gaugeNames.contains(Meters.EXTERNAL_TASKS_OPEN.getMeterName()));
        assertTrue(gaugeNames.contains(Meters.EXTERNAL_TASKS_OPEN_ERROR.getMeterName()));
    }

    @Test
    @Deployment(resources = "processes/external-task.bpmn")
    void testRetrieveGaugesDataWithRegularTask() {
        // Arrange
        processEngine.getRuntimeService().startProcessInstanceByKey("externalTaskProcess");
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
    @Deployment(resources = "processes/external-task.bpmn")
    void testRetrieveGaugesDataWithErrorTask() {
        // Arrange
        processEngine.getRuntimeService().startProcessInstanceByKey("externalTaskProcess");
        ExternalTaskQuery query = externalTaskService.createExternalTaskQuery();
        ExternalTask task = query.singleResult();
        assertNotNull(task);
        task.setErrorMessage("Test error");
        externalTaskService.handleFailure(task.getId(), "worker1", "Test error", 1, 0);

        // Act
        var gaugesData = monitor.retrieveGaugesData();

        // Assert
        assertNotNull(gaugesData);
        assertEquals(1, gaugesData.size());
        
        var data = gaugesData.iterator().next();
        assertEquals(1L, data.getGaugesValues().get(Meters.EXTERNAL_TASKS_OPEN.getMeterName()));
        assertEquals(1L, data.getGaugesValues().get(Meters.EXTERNAL_TASKS_OPEN_ERROR.getMeterName()));
    }

    @Test
    @Deployment(resources = "processes/external-task.bpmn")
    void testRetrieveGaugesDataWithMultipleTasks() {
        // Arrange
        processEngine.getRuntimeService().startProcessInstanceByKey("externalTaskProcess");
        processEngine.getRuntimeService().startProcessInstanceByKey("externalTaskProcess");
        ExternalTaskQuery query = externalTaskService.createExternalTaskQuery();
        List<ExternalTask> tasks = query.list();
        assertEquals(2, tasks.size());

        // Set error for one task
        ExternalTask task = tasks.get(0);
        task.setErrorMessage("Test error");
        externalTaskService.handleFailure(task.getId(), "worker1", "Test error", 1, 0);

        // Act
        var gaugesData = monitor.retrieveGaugesData();

        // Assert
        assertNotNull(gaugesData);
        assertEquals(1, gaugesData.size());
        
        var data = gaugesData.iterator().next();
        assertEquals(2L, data.getGaugesValues().get(Meters.EXTERNAL_TASKS_OPEN.getMeterName()));
        assertEquals(1L, data.getGaugesValues().get(Meters.EXTERNAL_TASKS_OPEN_ERROR.getMeterName()));
    }
} 