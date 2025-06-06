//package org.camunda.bpm.run.plugin.monitoring.task;
//
//import io.micrometer.core.instrument.MeterRegistry;
//import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
//import org.camunda.bpm.engine.ProcessEngine;
//import org.camunda.bpm.engine.TaskService;
//import org.camunda.bpm.engine.task.Task;
//import org.camunda.bpm.engine.test.Deployment;
//import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
//import org.camunda.bpm.run.plugin.monitoring.Meters;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(ProcessEngineExtension.class)
//class TaskSnapshotMonitorTest {
//    private ProcessEngine processEngine;
//
//    private TaskSnapshotMonitor monitor;
//
//    @BeforeEach
//    void setUp() {
//        MeterRegistry meterRegistry = new SimpleMeterRegistry();
//        monitor = new TaskSnapshotMonitor(processEngine, meterRegistry);
//    }
//
//    @Test
//    @Deployment(resources = "bpmn/task-process.bpmn")
//    void testRetrieveGaugesDataWithProcessInstanceTask() {
//        // Arrange
//        processEngine.getRuntimeService().startProcessInstanceByKey("taskProcess");
//        Task task = taskService.createTaskQuery().singleResult();
//        assertNotNull(task);
//
//        // Act
//        var gaugesData = monitor.retrieveGaugesData();
//
//        // Assert
//        assertNotNull(gaugesData);
//        assertEquals(1, gaugesData.size());
//
//        var data = gaugesData.iterator().next();
//        assertEquals(1L, data.getGaugesValues().get(Meters.TASKS_OPEN.getMeterName()));
//        assertTrue(data.getGaugesValues().get(Meters.TASKS_OPEN_NEWEST.getMeterName()) > 0);
//        assertTrue(data.getGaugesValues().get(Meters.TASKS_OPEN_OLDEST.getMeterName()) > 0);
//    }
//
//    @Test
//    @Deployment(resources = "processes/case-task.bpmn")
//    void testRetrieveGaugesDataWithCaseInstanceTask() {
//        // Arrange
//        processEngine.getCaseService().createCaseInstanceByKey("caseTask");
//        Task task = taskService.createTaskQuery().singleResult();
//        assertNotNull(task);
//
//        // Act
//        var gaugesData = monitor.retrieveGaugesData();
//
//        // Assert
//        assertNotNull(gaugesData);
//        assertEquals(1, gaugesData.size());
//
//        var data = gaugesData.iterator().next();
//        assertEquals(1L, data.getGaugesValues().get(Meters.TASKS_OPEN.getMeterName()));
//        assertTrue(data.getGaugesValues().get(Meters.TASKS_OPEN_NEWEST.getMeterName()) > 0);
//        assertTrue(data.getGaugesValues().get(Meters.TASKS_OPEN_OLDEST.getMeterName()) > 0);
//    }
//
//    @Test
//    void testRetrieveGaugesDataWithStandaloneTask() {
//        // Arrange
//        Task task = taskService.newTask();
//        task.setName("standalone-task");
//        taskService.saveTask(task);
//
//        // Act
//        var gaugesData = monitor.retrieveGaugesData();
//
//        // Assert
//        assertNotNull(gaugesData);
//        assertEquals(1, gaugesData.size());
//
//        var data = gaugesData.iterator().next();
//        assertEquals(1L, data.getGaugesValues().get(Meters.TASKS_OPEN.getMeterName()));
//        assertTrue(data.getGaugesValues().get(Meters.TASKS_OPEN_NEWEST.getMeterName()) > 0);
//        assertTrue(data.getGaugesValues().get(Meters.TASKS_OPEN_OLDEST.getMeterName()) > 0);
//
//        // Cleanup
//        taskService.deleteTask(task.getId());
//    }
//}
