package org.camunda.bpm.run.plugin.monitoring.incidents;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.Incident;
import org.camunda.bpm.run.plugin.monitoring.Meters;
import org.camunda.bpm.run.plugin.monitoring.Monitor;

import java.util.*;
import java.util.stream.Collectors;

public class IncidentSnapshotMonitor extends Monitor {

    public IncidentSnapshotMonitor(ProcessEngine processEngine, MeterRegistry meterRegistry) {
        super(processEngine, meterRegistry);
    }

    @Override
    protected List<String> getGaugeNames() {
        return Arrays.stream(Meters.values())
                .filter(meter -> meter.name().startsWith("INCIDENTS"))
                .map(Meters::getMeterName)
                .collect(Collectors.toList());
    }

    @Override
    protected Collection<MultiGaugeData> retrieveGaugesData() {
        Map<Tags, MultiGaugeData> map = new HashMap<>();
        List<Incident> incidents = getProcessEngine().getRuntimeService().createIncidentQuery().unlimitedList()
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

            // moved outside to prevent duplicate tags ( if the same process definition id )
            var tenantId = incident.getTenantId();
            var processDefinitionId = processDefinition.getId();
            var processDefinitionKey = processDefinition.getKey();
            var activityId = incident.getActivityId();
            var failedActivityId = incident.getActivityId();
            var incidentType = incident.getIncidentType();
            Tags tags = IncidentMeterTags.createTags(
                    tenantId == null ? "" : tenantId,
                    processDefinitionId == null ? "" : processDefinitionId,
                    processDefinitionKey == null ? "" : processDefinitionKey,
                    activityId == null ? "" : activityId,
                    failedActivityId == null ? "" : failedActivityId,
                    incidentType == null ? "" : incidentType
            ).and("topic.name", Objects.requireNonNullElse(topicName, ""));


            MultiGaugeData data = map.computeIfAbsent(tags, key -> {
                Map<String, Long> gaugeValues = new HashMap<>();
                gaugeValues.put(Meters.INCIDENTS_OPEN_NEWEST.getMeterName(), Long.MAX_VALUE);
                gaugeValues.put(Meters.INCIDENTS_OPEN_OLDEST.getMeterName(), Long.MIN_VALUE);
                gaugeValues.put(Meters.INCIDENTS_OPEN.getMeterName(), 0L);


                return new MultiGaugeData(gaugeValues, tags);
            });

            long ageSeconds = (now.getTime() - incident.getIncidentTimestamp().getTime()) / 1000;

            data.getGaugesValues().merge(Meters.INCIDENTS_OPEN_NEWEST.getMeterName(), ageSeconds, Long::min);
            data.getGaugesValues().merge(Meters.INCIDENTS_OPEN_OLDEST.getMeterName(), ageSeconds, Long::max);
            data.getGaugesValues().merge(Meters.INCIDENTS_OPEN.getMeterName(), 1L, Long::sum);
        }


        return map.values();
    }
}
