package org.camunda.bpm.run.plugin.monitoring.processinstance;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;
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
class ProcessInstanceSnapshotMonitorTest {

    @Autowired
    private ProcessEngine processEngine;

    private MeterRegistry meterRegistry;
    private ProcessInstanceSnapshotMonitor monitor;
    private RuntimeService runtimeService;


    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        monitor = new ProcessInstanceSnapshotMonitor(processEngine, meterRegistry);
        runtimeService = processEngine.getRuntimeService();

    }

    @Test
    void testGetGaugeNames() {
        // Act
        List<String> gaugeNames = monitor.getGaugeNames();

        // Assert
        assertEquals(2, gaugeNames.size());
        assertTrue(gaugeNames.contains(Meters.PROCESS_INSTANCES_RUNNING.getMeterName()));
        assertTrue(gaugeNames.contains(Meters.PROCESS_INSTANCES_SUSPENDED.getMeterName()));
    }

    @Test
    @Deployment(resources = "processes/task-process.bpmn")
    void testRetrieveGaugesDataWithRunningInstance() {
        // Arrange
        runtimeService.startProcessInstanceByKey("taskProcess");
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();
        ProcessInstance instance = query.singleResult();
        assertNotNull(instance);
        assertFalse(instance.isSuspended());

        // Act
        var gaugesData = monitor.retrieveGaugesData();

        // Assert
        assertNotNull(gaugesData);
        assertEquals(1, gaugesData.size());

        var data = gaugesData.iterator().next();
        assertEquals(1L, data.getGaugesValues().get(Meters.PROCESS_INSTANCES_RUNNING.getMeterName()));
        assertEquals(0L, data.getGaugesValues().get(Meters.PROCESS_INSTANCES_SUSPENDED.getMeterName()));
    }

    @Test
    @Deployment(resources = "processes/task-process.bpmn")
    void testRetrieveGaugesDataWithSuspendedInstance() {
        // Arrange
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("taskProcess");
        runtimeService.suspendProcessInstanceById(instance.getId());
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();
        ProcessInstance suspendedInstance = query.singleResult();
        assertNotNull(suspendedInstance);
        assertTrue(suspendedInstance.isSuspended());

        // Act
        var gaugesData = monitor.retrieveGaugesData();

        // Assert
        assertNotNull(gaugesData);
        assertEquals(1, gaugesData.size());

        var data = gaugesData.iterator().next();
        assertEquals(1L, data.getGaugesValues().get(Meters.PROCESS_INSTANCES_RUNNING.getMeterName()));
        assertEquals(1L, data.getGaugesValues().get(Meters.PROCESS_INSTANCES_SUSPENDED.getMeterName()));
    }

    @Test
    @Deployment(resources = "processes/task-process.bpmn")
    void testRetrieveGaugesDataWithMultipleInstances() {
        // Arrange
        runtimeService.startProcessInstanceByKey("taskProcess");
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("taskProcess");
        runtimeService.suspendProcessInstanceById(instance.getId());
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();
        List<ProcessInstance> instances = query.list();
        assertEquals(2, instances.size());

        // Act
        var gaugesData = monitor.retrieveGaugesData();

        // Assert
        assertNotNull(gaugesData);
        assertEquals(1, gaugesData.size());

        var data = gaugesData.iterator().next();
        assertEquals(2L, data.getGaugesValues().get(Meters.PROCESS_INSTANCES_RUNNING.getMeterName()));
        assertEquals(1L, data.getGaugesValues().get(Meters.PROCESS_INSTANCES_SUSPENDED.getMeterName()));
    }
} 