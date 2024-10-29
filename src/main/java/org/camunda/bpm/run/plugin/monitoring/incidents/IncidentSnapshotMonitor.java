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
        Map<String, MultiGaugeData> map = new HashMap<>();
        List<Incident> incidents = getProcessEngine().getRuntimeService().createIncidentQuery().unlimitedList();
        Date now = new Date();


        for (Incident incident : incidents) {
            String groupByKey = incident.getProcessDefinitionId();
            MultiGaugeData data = map.computeIfAbsent(groupByKey, key -> {
                Map<String, Long> gaugeValues = new HashMap<>();
                gaugeValues.put(Meters.INCIDENTS_OPEN_NEWEST.getMeterName(), Long.MAX_VALUE);
                gaugeValues.put(Meters.INCIDENTS_OPEN_OLDEST.getMeterName(), Long.MIN_VALUE);
                gaugeValues.put(Meters.INCIDENTS_OPEN.getMeterName(), 0L);

                ProcessDefinition processDefinition = getProcessDefinition(incident.getProcessDefinitionId());


                // Получаем имя топика внешней задачи, если это инцидент, связанный с внешней задачей
                String topicName = Optional.ofNullable(getProcessEngine().getExternalTaskService()
                                .createExternalTaskQuery()
                                .executionId(incident.getExecutionId())
                                .singleResult())
                        .map(ExternalTask::getTopicName)
                        .orElse(null);

                Tags tags = IncidentMeterTags.createTags(processDefinition.getTenantId(),
                        processDefinition.getId(),
                        processDefinition.getKey(),
                        incident.getActivityId(),
                        incident.getFailedActivityId(),
                        incident.getIncidentType());


                if (topicName != null) {
                    tags = tags.and("topic.name", topicName); // Добавляем тег для topicName
                }

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
