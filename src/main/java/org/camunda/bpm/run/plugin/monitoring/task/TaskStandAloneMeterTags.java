package org.camunda.bpm.run.plugin.monitoring.task;

import static org.camunda.bpm.run.plugin.monitoring.TagsChecker.addTagIfNotNull;

import io.micrometer.core.instrument.Tags;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskStandAloneMeterTags {

    TENANT_ID("tenant.id"), TASK_NAME("task.name");

    private final String tagName;

    public static Tags createTags(String tenantId, String taskName) {
        Tags tags = Tags.empty();
        tags = addTagIfNotNull(tags, TENANT_ID.getTagName(), tenantId);
        tags = addTagIfNotNull(tags, TASK_NAME.getTagName(), taskName);
        return tags;
    }
}
