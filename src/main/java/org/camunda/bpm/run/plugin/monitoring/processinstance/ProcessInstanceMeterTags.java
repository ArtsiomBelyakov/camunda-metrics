package org.camunda.bpm.run.plugin.monitoring.processinstance;

import static org.camunda.bpm.run.plugin.monitoring.TagsChecker.addTagIfNotNull;

import io.micrometer.core.instrument.Tags;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProcessInstanceMeterTags {

    TENANT_ID("tenant.id"),
    PROCESS_DEFINITION_KEY("process.definition.key");

    private final String tagName;

    public static Tags createTags(String tenantId, String processDefinitionKey) {
        Tags tags = Tags.empty();
        tags = addTagIfNotNull(tags, TENANT_ID.getTagName(), tenantId);
        tags = addTagIfNotNull(tags, PROCESS_DEFINITION_KEY.getTagName(), processDefinitionKey);
        return tags;
    }

}
