package org.camunda.bpm.run.plugin.monitoring.task;

import io.micrometer.core.instrument.Tags;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static org.camunda.bpm.run.plugin.monitoring.TagsChecker.checkNull;

@Getter
@AllArgsConstructor
public enum TaskCaseInstanceMeterTags {

    TENANT_ID("tenant.id"),
    CASE_DEFINITION_ID("case.definition.id"),
    TASK_DEFINITION_KEY("task.definition.key");

    private final String tagName;

    public static Tags createTags(String tenantId, String caseDefinitionIdString, String taskDefinitionKey) {
        Tags tags = Tags.empty();

        tags = tags.and(TaskCaseInstanceMeterTags.TENANT_ID.getTagName(), checkNull(tenantId, ""));
        tags = tags.and(TaskCaseInstanceMeterTags.CASE_DEFINITION_ID.getTagName(), checkNull(caseDefinitionIdString, ""));
        tags = tags.and(TaskCaseInstanceMeterTags.TASK_DEFINITION_KEY.getTagName(), checkNull(taskDefinitionKey, ""));

        return tags;

    }

}
