package org.camunda.bpm.run.plugin.monitoring.processinstance;

import io.micrometer.core.instrument.Tags;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static org.camunda.bpm.run.plugin.monitoring.TagsChecker.checkNull;

@Getter
@AllArgsConstructor
public enum ProcessInstanceMeterTags {

    TENANT_ID("tenant.id"),
    PROCESS_DEFINITION_ID("process.definition.id"),
    PROCESS_DEFINITION_KEY("process.definition.key");

    private final String tagName;

    public static Tags createTags(String tenantId, String processDefinitionId, String processDefinitionKey) {
        Tags tags = Tags.empty();

        tags = tags.and(TENANT_ID.getTagName(), checkNull(tenantId, ""));
        return tags
                .and(PROCESS_DEFINITION_ID.getTagName(), checkNull(processDefinitionId, ""))
                .and(PROCESS_DEFINITION_KEY.getTagName(), checkNull(processDefinitionKey, ""));
    }

}
