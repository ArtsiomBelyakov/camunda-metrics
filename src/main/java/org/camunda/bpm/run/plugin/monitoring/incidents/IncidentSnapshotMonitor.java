package org.camunda.bpm.run.plugin.monitoring.incidents;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.Incident;
import org.camunda.bpm.run.plugin.monitoring.Meters;
import org.camunda.bpm.run.plugin.monitoring.Monitor;

public class IncidentSnapshotMonitor extends Monitor {

    public IncidentSnapshotMonitor(ProcessEngine processEngine, MeterRegistry meterRegistry) {
        super(processEngine, meterRegistry);
    }

    @Override
    protected List<String> getGaugeNames() {
        return Arrays.stream(Meters.values())
            .filter(meter -> meter.name().startsWith("INCIDENTS"))
            .map(Meters::getMeterName)
            .toList();
    }

    @Override
    protected Collection<MultiGaugeData> retrieveGaugesData() {
        Map<Tags, MultiGaugeData> map = new HashMap<>();
        List<Incident> incidents = getProcessEngine().getRuntimeService().createIncidentQuery()
            .unlimitedList()
            .stream()
            .filter(incident -> incident.getId().equals(incident.getRootCauseIncidentId())
                || incident.getRootCauseIncidentId() == null)
            .toList();
        Date now = new Date();

        for (Incident incident : incidents) {
            ProcessDefinition processDefinition = getProcessDefinition(incident.getProcessDefinitionId());
            // Получаем имя топика внешней задачи, если это инцидент, связанный с внешней задачей
            String topicName = Optional.ofNullable(getProcessEngine().getExternalTaskService()
                    .createExternalTaskQuery()
                    .executionId(incident.getExecutionId())
                    .singleResult())
                .map(ExternalTask::getTopicName)
                .orElse(null);

            // moved outside to prevent duplicate tags ( if the same process definition key )
            var tenantId = incident.getTenantId();
            var processDefinitionKey = processDefinition.getKey();
            var activityId = incident.getActivityId();
            var failedActivityId = incident.getFailedActivityId();
            var incidentType = incident.getIncidentType();

            Tags baseTags = IncidentMeterTags.createTags(
                tenantId,
                processDefinitionKey,
                activityId,
                failedActivityId,
                incidentType
            );

            // Add topic name conditionally (only if not null)
            Tags tags = org.camunda.bpm.run.plugin.monitoring.TagsChecker.addTagIfNotNull(baseTags,
                "topic.name", topicName);

            MultiGaugeData data = map.computeIfAbsent(tags, key -> {
                Map<String, Long> gaugeValues = new HashMap<>();
                gaugeValues.put(Meters.INCIDENTS_OPEN_NEWEST.getMeterName(), Long.MAX_VALUE);
                gaugeValues.put(Meters.INCIDENTS_OPEN_OLDEST.getMeterName(), Long.MIN_VALUE);
                gaugeValues.put(Meters.INCIDENTS_OPEN.getMeterName(), 0L);

                return new MultiGaugeData(gaugeValues, tags);
            });

            long ageSeconds = (now.getTime() - incident.getIncidentTimestamp().getTime()) / 1000;

            data.getGaugesValues()
                .merge(Meters.INCIDENTS_OPEN_NEWEST.getMeterName(), ageSeconds, Long::min);
            data.getGaugesValues()
                .merge(Meters.INCIDENTS_OPEN_OLDEST.getMeterName(), ageSeconds, Long::max);
            data.getGaugesValues().merge(Meters.INCIDENTS_OPEN.getMeterName(), 1L, Long::sum);
        }

        return map.values();
    }
}
