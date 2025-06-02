package org.camunda.bpm.run.plugin.monitoring.externaltask;

import io.micrometer.core.instrument.Tags;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.function.Function;

@Getter
@AllArgsConstructor
public enum ExternalTaskMeterTags {

    TENANT_ID("tenant.id"),
    PROCESS_DEFINITION_ID("process.definition.id"),
    PROCESS_DEFINITION_KEY("process.definition.key"),
    ACTIVITY_ID("activity.id"),
    TOPIC_NAME("topic.name");

    private final String tagName;

    public static Tags createTags(String tenantId, String processDefinitionId, String processDefinitionKey,
            String activityId, String topicName) {
        Tags tags = Tags.empty();
        Function<String, String> check = string -> Objects.requireNonNullElse(string, "");
        tags = tags.and(ExternalTaskMeterTags.TENANT_ID.getTagName(), check.apply(tenantId));
        tags = tags.and(ExternalTaskMeterTags.PROCESS_DEFINITION_ID.getTagName(), check.apply(processDefinitionId));
        tags = tags.and(ExternalTaskMeterTags.PROCESS_DEFINITION_KEY.getTagName(), check.apply(processDefinitionKey));
        tags = tags.and(ExternalTaskMeterTags.ACTIVITY_ID.getTagName(), check.apply(activityId));
        tags = tags.and(ExternalTaskMeterTags.TOPIC_NAME.getTagName(), check.apply(topicName));

        return tags;

    }

}
