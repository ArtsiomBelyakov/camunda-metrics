package org.camunda.bpm.run.plugin.monitoring.task;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TaskCaseInstanceMeterTagsTest {

    @Test
    void testTagNames() {
        assertEquals("tenant.id", TaskCaseInstanceMeterTags.TENANT_ID.getTagName());
        assertEquals("case.definition.id", TaskCaseInstanceMeterTags.CASE_DEFINITION_ID.getTagName());
        assertEquals("task.definition.key", TaskCaseInstanceMeterTags.TASK_DEFINITION_KEY.getTagName());
    }

    @Test
    void testCreateTagsWithValidValues() {
        // Arrange
        String tenantId = "test-tenant";
        String caseDefinitionId = "test-case-id";
        String taskDefinitionKey = "test-task-key";

        // Act
        Tags tags = TaskCaseInstanceMeterTags.createTags(tenantId, caseDefinitionId, taskDefinitionKey);

        // Assert
        Map<String, String> tagMap = tags.stream()
            .collect(Collectors.toMap(Tag::getKey, Tag::getValue));
        assertEquals(tenantId, tagMap.get("tenant.id"));
        assertEquals(caseDefinitionId, tagMap.get("case.definition.id"));
        assertEquals(taskDefinitionKey, tagMap.get("task.definition.key"));
    }

    @Test
    void testCreateTagsWithNullValues() {
        // Act
        Tags tags = TaskCaseInstanceMeterTags.createTags(null, null, null);

        // Assert
        Map<String, String> tagMap = tags.stream()
            .collect(Collectors.toMap(Tag::getKey, Tag::getValue));
        assertEquals("", tagMap.get("tenant.id"));
        assertEquals("", tagMap.get("case.definition.id"));
        assertEquals("", tagMap.get("task.definition.key"));
    }

    @Test
    void testCreateTagsWithMixedValues() {
        // Arrange
        String tenantId = "test-tenant";
        String caseDefinitionId = null;
        String taskDefinitionKey = "test-task-key";

        // Act
        Tags tags = TaskCaseInstanceMeterTags.createTags(tenantId, caseDefinitionId, taskDefinitionKey);

        // Assert
        Map<String, String> tagMap = tags.stream()
            .collect(Collectors.toMap(Tag::getKey, Tag::getValue));
        assertEquals(tenantId, tagMap.get("tenant.id"));
        assertEquals("", tagMap.get("case.definition.id"));
        assertEquals(taskDefinitionKey, tagMap.get("task.definition.key"));
    }
} 