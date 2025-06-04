package org.camunda.bpm.run.plugin.monitoring.task;

import io.micrometer.core.instrument.Tags;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static org.camunda.bpm.run.plugin.monitoring.TagsChecker.checkNull;

@Getter
@AllArgsConstructor
public enum TaskStandAloneMeterTags {

    TENANT_ID("tenant.id"), TASK_NAME("task.name");

    private final String tagName;

    public static Tags createTags(String tenantId, String taskName) {
        Tags tags = Tags.empty();

        tags = tags.and(TaskStandAloneMeterTags.TENANT_ID.getTagName(), checkNull(tenantId, ""));
        tags = tags.and(TaskStandAloneMeterTags.TASK_NAME.getTagName(), checkNull(taskName, ""));

        return tags;

    }

}
