package org.camunda.bpm.run.plugin.monitoring.incidents;

import io.micrometer.core.instrument.Tags;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.function.Function;

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
        Function<String, String> check = string -> Objects.requireNonNullElse(string, "");

        tags = tags.and(IncidentMeterTags.TENANT_ID.getTagName(), check.apply(tenantId));
        tags = tags.and(IncidentMeterTags.PROCESS_DEFINITION_ID.getTagName(), check.apply(processDefinitionId));
        tags = tags.and(IncidentMeterTags.PROCESS_DEFINITION_KEY.getTagName(), check.apply(processDefinitionKey));
        tags = tags.and(IncidentMeterTags.ACTIVITY_ID.getTagName(), check.apply(activityId));
        tags = tags.and(IncidentMeterTags.FAILED_ACTIVITY_ID.getTagName(), check.apply(failedActivityId));
        tags = tags.and(IncidentMeterTags.INCIDENT_TYPE.getTagName(), check.apply(incidentType));

        return tags;

    }

}
