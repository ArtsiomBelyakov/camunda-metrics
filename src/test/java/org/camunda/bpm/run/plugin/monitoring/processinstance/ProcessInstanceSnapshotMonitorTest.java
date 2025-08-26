package org.camunda.bpm.run.plugin.monitoring.processinstance;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.camunda.bpm.run.plugin.monitoring.Meters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ProcessEngineExtension.class)
class ProcessInstanceSnapshotMonitorTest {
    private ProcessEngine processEngine;

    private ProcessInstanceSnapshotMonitor monitor;
    private RuntimeService runtimeService;


    @BeforeEach
    void setUp() {
        MeterRegistry meterRegistry = new SimpleMeterRegistry();
        monitor = new ProcessInstanceSnapshotMonitor(processEngine, meterRegistry);
        runtimeService = processEngine.getRuntimeService();
    }

    @Test
    void testGetGaugeNames() {
        // Act
        List<String> gaugeNames = monitor.getGaugeNames();

        // Assert
        assertThat(gaugeNames).containsExactlyInAnyOrder(
                Meters.PROCESS_INSTANCES_RUNNING.getMeterName(),
                Meters.PROCESS_INSTANCES_SUSPENDED.getMeterName()
        );
    }

    @Test
    @Deployment(resources = "bpmn/test.process.bpmn")
    void testRetrieveGaugesDataWithRunningInstance() {
        // Arrange
        runtimeService.startProcessInstanceByKey("test.process");
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();
        ProcessInstance instance = query.active().singleResult();
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
    @Deployment(resources = "bpmn/test.process.bpmn")
    void testRetrieveGaugesDataWithSuspendedInstance() {
        // Arrange
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("test.process");
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
    @Deployment(resources = "bpmn/test.process.bpmn")
    void testRetrieveGaugesDataWithMultipleInstances() {
        // Arrange
        runtimeService.startProcessInstanceByKey("test.process");
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("test.process");
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
