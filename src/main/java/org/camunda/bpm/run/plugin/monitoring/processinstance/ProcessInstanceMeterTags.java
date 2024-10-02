package org.camunda.bpm.run.plugin.monitoring.processinstance;

import io.micrometer.core.instrument.Tags;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ProcessInstanceMeterTags {

    TENANT_ID("tenant.id"),
    PROCESS_DEFINITION_ID("process.definition.id"),
    PROCESS_DEFINITION_KEY("process.definition.key");

    @Getter
    private String tagName;

    public static Tags createTags(String tenantId, String processDefinitionId, String processDefinitionKey) {
        Tags tags = Tags.empty();
        if (tenantId != null) {
            tags.and(TENANT_ID.getTagName(), tenantId);
        }
        return tags.and(PROCESS_DEFINITION_ID.getTagName(), processDefinitionId).and(
                PROCESS_DEFINITION_KEY.getTagName(),
                processDefinitionKey);

    }

}
