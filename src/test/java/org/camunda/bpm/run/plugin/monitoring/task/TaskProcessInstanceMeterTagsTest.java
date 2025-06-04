package org.camunda.bpm.run.plugin.monitoring.task;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TaskProcessInstanceMeterTagsTest {

    @Test
    void testTagNames() {
        assertEquals("tenant.id", TaskProcessInstanceMeterTags.TENANT_ID.getTagName());
        assertEquals("process.definition.id", TaskProcessInstanceMeterTags.PROCESS_DEFINITION_ID.getTagName());
        assertEquals("process.definition.key", TaskProcessInstanceMeterTags.PROCESS_DEFINITION_KEY.getTagName());
        assertEquals("task.definition.key", TaskProcessInstanceMeterTags.TASK_DEFINITION_KEY.getTagName());
    }

    @Test
    void testCreateTagsWithValidValues() {
        // Arrange
        String tenantId = "test-tenant";
        String processDefinitionId = "test-process-id";
        String processDefinitionKey = "test-process-key";
        String taskDefinitionKey = "test-task-key";

        // Act
        Tags tags = TaskProcessInstanceMeterTags.createTags(
            tenantId, processDefinitionId, processDefinitionKey, taskDefinitionKey);

        // Assert
        Map<String, String> tagMap = tags.stream()
            .collect(Collectors.toMap(Tag::getKey, Tag::getValue));
        assertEquals(tenantId, tagMap.get("tenant.id"));
        assertEquals(processDefinitionId, tagMap.get("process.definition.id"));
        assertEquals(processDefinitionKey, tagMap.get("process.definition.key"));
        assertEquals(taskDefinitionKey, tagMap.get("task.definition.key"));
    }

    @Test
    void testCreateTagsWithNullValues() {
        // Act
        Tags tags = TaskProcessInstanceMeterTags.createTags(null, null, null, null);

        // Assert
        Map<String, String> tagMap = tags.stream()
            .collect(Collectors.toMap(Tag::getKey, Tag::getValue));
        assertEquals("", tagMap.get("tenant.id"));
        assertEquals("", tagMap.get("process.definition.id"));
        assertEquals("", tagMap.get("process.definition.key"));
        assertEquals("", tagMap.get("task.definition.key"));
    }

    @Test
    void testCreateTagsWithMixedValues() {
        // Arrange
        String tenantId = "test-tenant";
        String processDefinitionId = null;
        String processDefinitionKey = "test-process-key";
        String taskDefinitionKey = null;

        // Act
        Tags tags = TaskProcessInstanceMeterTags.createTags(
            tenantId, processDefinitionId, processDefinitionKey, taskDefinitionKey);

        // Assert
        Map<String, String> tagMap = tags.stream()
            .collect(Collectors.toMap(Tag::getKey, Tag::getValue));
        assertEquals(tenantId, tagMap.get("tenant.id"));
        assertEquals("", tagMap.get("process.definition.id"));
        assertEquals(processDefinitionKey, tagMap.get("process.definition.key"));
        assertEquals("", tagMap.get("task.definition.key"));
    }
} 