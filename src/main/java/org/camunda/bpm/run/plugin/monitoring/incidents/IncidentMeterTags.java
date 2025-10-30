package org.camunda.bpm.run.plugin.monitoring.incidents;

import static org.camunda.bpm.run.plugin.monitoring.TagsChecker.addTagIfNotNull;

import io.micrometer.core.instrument.Tags;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IncidentMeterTags {

    TENANT_ID("tenant.id"),
    PROCESS_DEFINITION_KEY("process.definition.key"),
    ACTIVITY_ID("activity.id"),
    FAILED_ACTIVITY_ID("failed.activity.id"),
    INCIDENT_TYPE("incident.type");

    private final String tagName;

    public static Tags createTags(String tenantId, String processDefinitionKey,
        String activityId, String failedActivityId, String incidentType) {
        Tags tags = Tags.empty();
        tags = addTagIfNotNull(tags, TENANT_ID.getTagName(), tenantId);
        tags = addTagIfNotNull(tags, PROCESS_DEFINITION_KEY.getTagName(), processDefinitionKey);
        tags = addTagIfNotNull(tags, ACTIVITY_ID.getTagName(), activityId);
        tags = addTagIfNotNull(tags, FAILED_ACTIVITY_ID.getTagName(), failedActivityId);
        tags = addTagIfNotNull(tags, INCIDENT_TYPE.getTagName(), incidentType);
        return tags;
    }

}
