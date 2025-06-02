package org.camunda.bpm.run.plugin.monitoring.processinstance;

import io.micrometer.core.instrument.Tags;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.function.Function;

@Getter
@AllArgsConstructor
public enum ProcessInstanceMeterTags {

    TENANT_ID("tenant.id"),
    PROCESS_DEFINITION_ID("process.definition.id"),
    PROCESS_DEFINITION_KEY("process.definition.key");

    private final String tagName;

    public static Tags createTags(String tenantId, String processDefinitionId, String processDefinitionKey) {
        Tags tags = Tags.empty();
        Function<String, String> check = string -> Objects.requireNonNullElse(string, "");
        tags = tags.and(TENANT_ID.getTagName(), check.apply(tenantId));
        return tags
                .and(PROCESS_DEFINITION_ID.getTagName(), check.apply(processDefinitionId))
                .and(PROCESS_DEFINITION_KEY.getTagName(), check.apply(processDefinitionKey));
    }

}
