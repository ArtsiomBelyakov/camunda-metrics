package org.camunda.bpm.run.plugin.monitoring.incidents;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class IncidentMeterTagsTest {

    @Test
    void testTagNames() {
        assertEquals("tenant.id", IncidentMeterTags.TENANT_ID.getTagName());
        assertEquals("process.definition.id", IncidentMeterTags.PROCESS_DEFINITION_ID.getTagName());
        assertEquals("process.definition.key", IncidentMeterTags.PROCESS_DEFINITION_KEY.getTagName());
        assertEquals("activity.id", IncidentMeterTags.ACTIVITY_ID.getTagName());
        assertEquals("failed.activity.id", IncidentMeterTags.FAILED_ACTIVITY_ID.getTagName());
        assertEquals("incident.type", IncidentMeterTags.INCIDENT_TYPE.getTagName());
    }

    @Test
    void testCreateTagsWithValidValues() {
        // Arrange
        String tenantId = "test-tenant";
        String processDefinitionId = "test-process-id";
        String processDefinitionKey = "test-process-key";
        String activityId = "test-activity";
        String failedActivityId = "test-failed-activity";
        String incidentType = "test-incident-type";

        // Act
        Tags tags = IncidentMeterTags.createTags(
            tenantId, processDefinitionId, processDefinitionKey, 
            activityId, failedActivityId, incidentType);

        // Assert
        Map<String, String> tagMap = tags.stream()
            .collect(Collectors.toMap(Tag::getKey, Tag::getValue));
        assertEquals(tenantId, tagMap.get("tenant.id"));
        assertEquals(processDefinitionId, tagMap.get("process.definition.id"));
        assertEquals(processDefinitionKey, tagMap.get("process.definition.key"));
        assertEquals(activityId, tagMap.get("activity.id"));
        assertEquals(failedActivityId, tagMap.get("failed.activity.id"));
        assertEquals(incidentType, tagMap.get("incident.type"));
    }

    @Test
    void testCreateTagsWithNullValues() {
        // Act
        Tags tags = IncidentMeterTags.createTags(null, null, null, null, null, null);

        // Assert
        Map<String, String> tagMap = tags.stream()
            .collect(Collectors.toMap(Tag::getKey, Tag::getValue));
        assertEquals("", tagMap.get("tenant.id"));
        assertEquals("", tagMap.get("process.definition.id"));
        assertEquals("", tagMap.get("process.definition.key"));
        assertEquals("", tagMap.get("activity.id"));
        assertEquals("", tagMap.get("failed.activity.id"));
        assertEquals("", tagMap.get("incident.type"));
    }

    @Test
    void testCreateTagsWithMixedValues() {
        // Arrange
        String tenantId = "test-tenant";
        String processDefinitionId = null;
        String processDefinitionKey = "test-process-key";
        String activityId = "test-activity";
        String failedActivityId = null;
        String incidentType = "test-incident-type";

        // Act
        Tags tags = IncidentMeterTags.createTags(
            tenantId, processDefinitionId, processDefinitionKey, 
            activityId, failedActivityId, incidentType);

        // Assert
        Map<String, String> tagMap = tags.stream()
            .collect(Collectors.toMap(Tag::getKey, Tag::getValue));
        assertEquals(tenantId, tagMap.get("tenant.id"));
        assertEquals("", tagMap.get("process.definition.id"));
        assertEquals(processDefinitionKey, tagMap.get("process.definition.key"));
        assertEquals(activityId, tagMap.get("activity.id"));
        assertEquals("", tagMap.get("failed.activity.id"));
        assertEquals(incidentType, tagMap.get("incident.type"));
    }
} 