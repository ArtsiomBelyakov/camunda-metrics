package org.camunda.bpm.run.plugin.monitoring.externaltask;

import io.micrometer.core.instrument.Tags;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static org.camunda.bpm.run.plugin.monitoring.TagsChecker.checkNull;

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

        tags = tags.and(ExternalTaskMeterTags.TENANT_ID.getTagName(), checkNull(tenantId, ""));
        tags = tags.and(ExternalTaskMeterTags.PROCESS_DEFINITION_ID.getTagName(), checkNull(processDefinitionId, ""));
        tags = tags.and(ExternalTaskMeterTags.PROCESS_DEFINITION_KEY.getTagName(), checkNull(processDefinitionKey, ""));
        tags = tags.and(ExternalTaskMeterTags.ACTIVITY_ID.getTagName(), checkNull(activityId, ""));
        tags = tags.and(ExternalTaskMeterTags.TOPIC_NAME.getTagName(), checkNull(topicName, ""));

        return tags;

    }

}
