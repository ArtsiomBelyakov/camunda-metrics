package org.camunda.bpm.run.plugin.monitoring.task;

import static org.camunda.bpm.run.plugin.monitoring.TagsChecker.addTagIfNotNull;

import io.micrometer.core.instrument.Tags;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskProcessInstanceMeterTags {

    TENANT_ID("tenant.id"),
    PROCESS_DEFINITION_KEY("process.definition.key"),
    TASK_DEFINITION_KEY("task.definition.key");

    private final String tagName;

    public static Tags createTags(String tenantId, String processDefinitionKey,
        String taskDefinitionKey) {
        Tags tags = Tags.empty();
        tags = addTagIfNotNull(tags, TENANT_ID.getTagName(), tenantId);
        tags = addTagIfNotNull(tags, PROCESS_DEFINITION_KEY.getTagName(), processDefinitionKey);
        tags = addTagIfNotNull(tags, TASK_DEFINITION_KEY.getTagName(), taskDefinitionKey);
        return tags;
    }

}
