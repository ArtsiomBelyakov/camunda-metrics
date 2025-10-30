package org.camunda.bpm.run.plugin.monitoring.task;

import static org.camunda.bpm.run.plugin.monitoring.TagsChecker.addTagIfNotNull;

import io.micrometer.core.instrument.Tags;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskCaseInstanceMeterTags {

    TENANT_ID("tenant.id"),
    CASE_DEFINITION_ID("case.definition.id"),
    TASK_DEFINITION_KEY("task.definition.key");

    private final String tagName;

    public static Tags createTags(String tenantId, String caseDefinitionIdString,
        String taskDefinitionKey) {
        Tags tags = Tags.empty();
        tags = addTagIfNotNull(tags, TENANT_ID.getTagName(), tenantId);
        tags = addTagIfNotNull(tags, CASE_DEFINITION_ID.getTagName(), caseDefinitionIdString);
        tags = addTagIfNotNull(tags, TASK_DEFINITION_KEY.getTagName(), taskDefinitionKey);
        return tags;
    }

}
