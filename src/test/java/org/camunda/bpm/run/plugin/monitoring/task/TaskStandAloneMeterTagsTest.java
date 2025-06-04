package org.camunda.bpm.run.plugin.monitoring.task;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TaskStandAloneMeterTagsTest {

    @Test
    void testTagNames() {
        assertEquals("tenant.id", TaskStandAloneMeterTags.TENANT_ID.getTagName());
        assertEquals("task.name", TaskStandAloneMeterTags.TASK_NAME.getTagName());
    }

    @Test
    void testCreateTagsWithValidValues() {
        // Arrange
        String tenantId = "test-tenant";
        String taskName = "test-task";

        // Act
        Tags tags = TaskStandAloneMeterTags.createTags(tenantId, taskName);

        // Assert
        Map<String, String> tagMap = tags.stream()
            .collect(Collectors.toMap(Tag::getKey, Tag::getValue));
        assertEquals(tenantId, tagMap.get("tenant.id"));
        assertEquals(taskName, tagMap.get("task.name"));
    }

    @Test
    void testCreateTagsWithNullValues() {
        // Act
        Tags tags = TaskStandAloneMeterTags.createTags(null, null);

        // Assert
        Map<String, String> tagMap = tags.stream()
            .collect(Collectors.toMap(Tag::getKey, Tag::getValue));
        assertEquals("", tagMap.get("tenant.id"));
        assertEquals("", tagMap.get("task.name"));
    }

    @Test
    void testCreateTagsWithMixedValues() {
        // Arrange
        String tenantId = "test-tenant";
        String taskName = null;

        // Act
        Tags tags = TaskStandAloneMeterTags.createTags(tenantId, taskName);

        // Assert
        Map<String, String> tagMap = tags.stream()
            .collect(Collectors.toMap(Tag::getKey, Tag::getValue));
        assertEquals(tenantId, tagMap.get("tenant.id"));
        assertEquals("", tagMap.get("task.name"));
    }
} 