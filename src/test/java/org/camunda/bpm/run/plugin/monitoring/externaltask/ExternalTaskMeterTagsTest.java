package org.camunda.bpm.run.plugin.monitoring.externaltask;

import io.micrometer.core.instrument.Tags;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExternalTaskMeterTagsTest {

    @Test
    void testTagNames() {
        // Assert
        assertEquals("tenant.id", ExternalTaskMeterTags.TENANT_ID.getTagName());
        assertEquals("process.definition.id", ExternalTaskMeterTags.PROCESS_DEFINITION_ID.getTagName());
        assertEquals("process.definition.key", ExternalTaskMeterTags.PROCESS_DEFINITION_KEY.getTagName());
        assertEquals("activity.id", ExternalTaskMeterTags.ACTIVITY_ID.getTagName());
        assertEquals("topic.name", ExternalTaskMeterTags.TOPIC_NAME.getTagName());
    }

    @Test
    void testCreateTagsWithValidValues() {
        // Arrange
        String tenantId = "tenant-1";
        String processDefinitionId = "process-definition-1";
        String processDefinitionKey = "process-key-1";
        String activityId = "activity-1";
        String topicName = "test-topic";

        // Act
        Tags tags = ExternalTaskMeterTags.createTags(tenantId, processDefinitionId, processDefinitionKey, activityId, topicName);

        // Assert
        assertEquals(tenantId, tags.stream().filter(tag -> tag.getKey().equals(ExternalTaskMeterTags.TENANT_ID.getTagName())).findFirst().get().getValue());
        assertEquals(processDefinitionId, tags.stream().filter(tag -> tag.getKey().equals(ExternalTaskMeterTags.PROCESS_DEFINITION_ID.getTagName())).findFirst().get().getValue());
        assertEquals(processDefinitionKey, tags.stream().filter(tag -> tag.getKey().equals(ExternalTaskMeterTags.PROCESS_DEFINITION_KEY.getTagName())).findFirst().get().getValue());
        assertEquals(activityId, tags.stream().filter(tag -> tag.getKey().equals(ExternalTaskMeterTags.ACTIVITY_ID.getTagName())).findFirst().get().getValue());
        assertEquals(topicName, tags.stream().filter(tag -> tag.getKey().equals(ExternalTaskMeterTags.TOPIC_NAME.getTagName())).findFirst().get().getValue());
    }

    @Test
    void testCreateTagsWithNullValues() {
        // Act
        Tags tags = ExternalTaskMeterTags.createTags(null, null, null, null, null);

        // Assert
        assertEquals("", tags.stream().filter(tag -> tag.getKey().equals(ExternalTaskMeterTags.TENANT_ID.getTagName())).findFirst().get().getValue());
        assertEquals("", tags.stream().filter(tag -> tag.getKey().equals(ExternalTaskMeterTags.PROCESS_DEFINITION_ID.getTagName())).findFirst().get().getValue());
        assertEquals("", tags.stream().filter(tag -> tag.getKey().equals(ExternalTaskMeterTags.PROCESS_DEFINITION_KEY.getTagName())).findFirst().get().getValue());
        assertEquals("", tags.stream().filter(tag -> tag.getKey().equals(ExternalTaskMeterTags.ACTIVITY_ID.getTagName())).findFirst().get().getValue());
        assertEquals("", tags.stream().filter(tag -> tag.getKey().equals(ExternalTaskMeterTags.TOPIC_NAME.getTagName())).findFirst().get().getValue());
    }

    @Test
    void testCreateTagsWithMixedValues() {
        // Arrange
        String tenantId = "tenant-1";
        String processDefinitionId = null;
        String processDefinitionKey = "process-key-1";
        String activityId = null;
        String topicName = "test-topic";

        // Act
        Tags tags = ExternalTaskMeterTags.createTags(tenantId, processDefinitionId, processDefinitionKey, activityId, topicName);

        // Assert
        assertEquals(tenantId, tags.stream().filter(tag -> tag.getKey().equals(ExternalTaskMeterTags.TENANT_ID.getTagName())).findFirst().get().getValue());
        assertEquals("", tags.stream().filter(tag -> tag.getKey().equals(ExternalTaskMeterTags.PROCESS_DEFINITION_ID.getTagName())).findFirst().get().getValue());
        assertEquals(processDefinitionKey, tags.stream().filter(tag -> tag.getKey().equals(ExternalTaskMeterTags.PROCESS_DEFINITION_KEY.getTagName())).findFirst().get().getValue());
        assertEquals("", tags.stream().filter(tag -> tag.getKey().equals(ExternalTaskMeterTags.ACTIVITY_ID.getTagName())).findFirst().get().getValue());
        assertEquals(topicName, tags.stream().filter(tag -> tag.getKey().equals(ExternalTaskMeterTags.TOPIC_NAME.getTagName())).findFirst().get().getValue());
    }
} 