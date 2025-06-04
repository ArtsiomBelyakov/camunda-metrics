package org.camunda.bpm.run.plugin.monitoring.processinstance;

import io.micrometer.core.instrument.Tags;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProcessInstanceMeterTagsTest {

    @Test
    void testTagNames() {
        // Assert
        assertEquals("tenant.id", ProcessInstanceMeterTags.TENANT_ID.getTagName());
        assertEquals("process.definition.id", ProcessInstanceMeterTags.PROCESS_DEFINITION_ID.getTagName());
        assertEquals("process.definition.key", ProcessInstanceMeterTags.PROCESS_DEFINITION_KEY.getTagName());
    }

    @Test
    void testCreateTagsWithValidValues() {
        // Arrange
        String tenantId = "tenant-1";
        String processDefinitionId = "process-definition-1";
        String processDefinitionKey = "process-key-1";

        // Act
        Tags tags = ProcessInstanceMeterTags.createTags(tenantId, processDefinitionId, processDefinitionKey);

        // Assert
        assertEquals(tenantId, tags.stream().filter(tag -> tag.getKey().equals(ProcessInstanceMeterTags.TENANT_ID.getTagName())).findFirst().get().getValue());
        assertEquals(processDefinitionId, tags.stream().filter(tag -> tag.getKey().equals(ProcessInstanceMeterTags.PROCESS_DEFINITION_ID.getTagName())).findFirst().get().getValue());
        assertEquals(processDefinitionKey, tags.stream().filter(tag -> tag.getKey().equals(ProcessInstanceMeterTags.PROCESS_DEFINITION_KEY.getTagName())).findFirst().get().getValue());
    }

    @Test
    void testCreateTagsWithNullValues() {
        // Act
        Tags tags = ProcessInstanceMeterTags.createTags(null, null, null);

        // Assert
        assertEquals("", tags.stream().filter(tag -> tag.getKey().equals(ProcessInstanceMeterTags.TENANT_ID.getTagName())).findFirst().get().getValue());
        assertEquals("", tags.stream().filter(tag -> tag.getKey().equals(ProcessInstanceMeterTags.PROCESS_DEFINITION_ID.getTagName())).findFirst().get().getValue());
        assertEquals("", tags.stream().filter(tag -> tag.getKey().equals(ProcessInstanceMeterTags.PROCESS_DEFINITION_KEY.getTagName())).findFirst().get().getValue());
    }

    @Test
    void testCreateTagsWithMixedValues() {
        // Arrange
        String tenantId = "tenant-1";
        String processDefinitionId = null;
        String processDefinitionKey = "process-key-1";

        // Act
        Tags tags = ProcessInstanceMeterTags.createTags(tenantId, processDefinitionId, processDefinitionKey);

        // Assert
        assertEquals(tenantId, tags.stream().filter(tag -> tag.getKey().equals(ProcessInstanceMeterTags.TENANT_ID.getTagName())).findFirst().get().getValue());
        assertEquals("", tags.stream().filter(tag -> tag.getKey().equals(ProcessInstanceMeterTags.PROCESS_DEFINITION_ID.getTagName())).findFirst().get().getValue());
        assertEquals(processDefinitionKey, tags.stream().filter(tag -> tag.getKey().equals(ProcessInstanceMeterTags.PROCESS_DEFINITION_KEY.getTagName())).findFirst().get().getValue());
    }
} 