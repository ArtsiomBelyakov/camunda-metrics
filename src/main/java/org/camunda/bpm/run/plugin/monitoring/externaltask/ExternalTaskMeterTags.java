package org.camunda.bpm.run.plugin.monitoring.externaltask;

import static org.camunda.bpm.run.plugin.monitoring.TagsChecker.addTagIfNotNull;

import io.micrometer.core.instrument.Tags;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExternalTaskMeterTags {

    TENANT_ID("tenant.id"),
    PROCESS_DEFINITION_KEY("process.definition.key"),
    ACTIVITY_ID("activity.id"),
    TOPIC_NAME("topic.name");

    private final String tagName;

    public static Tags createTags(String tenantId, String processDefinitionKey,
        String activityId, String topicName) {
        Tags tags = Tags.empty();
        tags = addTagIfNotNull(tags, TENANT_ID.getTagName(), tenantId);
        tags = addTagIfNotNull(tags, PROCESS_DEFINITION_KEY.getTagName(), processDefinitionKey);
        tags = addTagIfNotNull(tags, ACTIVITY_ID.getTagName(), activityId);
        tags = addTagIfNotNull(tags, TOPIC_NAME.getTagName(), topicName);
        return tags;
    }
}
