package org.camunda.bpm.run.plugin.monitoring.task;

import io.micrometer.core.instrument.Tags;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static org.camunda.bpm.run.plugin.monitoring.TagsChecker.checkNull;

@Getter
@AllArgsConstructor
public enum TaskProcessInstanceMeterTags {

    TENANT_ID("tenant.id"),
    PROCESS_DEFINITION_ID("process.definition.id"),
    PROCESS_DEFINITION_KEY("process.definition.key"),
    TASK_DEFINITION_KEY("task.definition.key");

    private final String tagName;

    public static Tags createTags(String tenantId, String processDefinitionId, String processDefinitionKey, String taskDefinitionKey) {
        Tags tags = Tags.empty();

        tags = tags.and(TaskProcessInstanceMeterTags.TENANT_ID.getTagName(), checkNull(tenantId, ""));
        tags = tags.and(TaskProcessInstanceMeterTags.PROCESS_DEFINITION_ID.getTagName(), checkNull(processDefinitionId, ""));
        tags = tags.and(TaskProcessInstanceMeterTags.PROCESS_DEFINITION_KEY.getTagName(), checkNull(processDefinitionKey, ""));
        tags = tags.and(TaskProcessInstanceMeterTags.TASK_DEFINITION_KEY.getTagName(), checkNull(taskDefinitionKey, ""));

        return tags;

    }

}
