package org.camunda.bpm.run.plugin.monitoring.incidents;

import io.micrometer.core.instrument.Tags;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static org.camunda.bpm.run.plugin.monitoring.TagsChecker.checkNull;

@Getter
@AllArgsConstructor
public enum IncidentMeterTags {

    TENANT_ID("tenant.id"),
    PROCESS_DEFINITION_ID("process.definition.id"),
    PROCESS_DEFINITION_KEY("process.definition.key"),
    ACTIVITY_ID("activity.id"),
    FAILED_ACTIVITY_ID("failed.activity.id"),
    INCIDENT_TYPE("incident.type");

    private final String tagName;

    public static Tags createTags(String tenantId, String processDefinitionId, String processDefinitionKey,
                                  String activityId, String failedActivityId, String incidentType) {
        Tags tags = Tags.empty();

        tags = tags.and(IncidentMeterTags.TENANT_ID.getTagName(), checkNull(tenantId, ""));
        tags = tags.and(IncidentMeterTags.PROCESS_DEFINITION_ID.getTagName(), checkNull(processDefinitionId, ""));
        tags = tags.and(IncidentMeterTags.PROCESS_DEFINITION_KEY.getTagName(), checkNull(processDefinitionKey, ""));
        tags = tags.and(IncidentMeterTags.ACTIVITY_ID.getTagName(), checkNull(activityId, ""));
        tags = tags.and(IncidentMeterTags.FAILED_ACTIVITY_ID.getTagName(), checkNull(failedActivityId, ""));
        tags = tags.and(IncidentMeterTags.INCIDENT_TYPE.getTagName(), checkNull(incidentType, ""));

        return tags;

    }

}
