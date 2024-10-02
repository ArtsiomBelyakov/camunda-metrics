package org.camunda.bpm.run.plugin.monitoring.externaltask;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.run.plugin.monitoring.Meters;
import org.camunda.bpm.run.plugin.monitoring.Monitor;

import java.util.*;
import java.util.stream.Collectors;

public class ExternalTaskSnapshotMonitor extends Monitor {

    public ExternalTaskSnapshotMonitor(ProcessEngine processEngine, MeterRegistry meterRegistry) {
        super(processEngine, meterRegistry);
    }

    @Override
    protected List<String> getGaugeNames() {
        return Arrays.asList(Meters.EXTERNAL_TASKS_OPEN, Meters.EXTERNAL_TASKS_OPEN_ERROR).stream()
                .map(Meters::getMeterName).collect(Collectors.toList());
    }

    @Override
    protected Collection<MultiGaugeData> retrieveGaugesData() {
        Map<Tags, MultiGaugeData> map = new HashMap<>();
        List<ExternalTask> tasks = getProcessEngine().getExternalTaskService().createExternalTaskQuery()
                .unlimitedList();

        for (ExternalTask task : tasks) {
            Tags tags = ExternalTaskMeterTags.createTags(task.getTenantId(), task.getProcessDefinitionId(),
                    task.getProcessDefinitionKey(), task.getActivityId(), task.getTopicName());
            MultiGaugeData data = map.get(tags);

            if (data == null) {
                Map<String, Long> gaugeValues = new HashMap<>();
                gaugeValues.put(Meters.EXTERNAL_TASKS_OPEN.getMeterName(), 0L);
                gaugeValues.put(Meters.EXTERNAL_TASKS_OPEN_ERROR.getMeterName(), 0L);

                data = new MultiGaugeData(gaugeValues, getTagsForProcessDefinition(task.getProcessDefinitionId()));
            }

            data.getGaugesValues().merge(Meters.EXTERNAL_TASKS_OPEN.getMeterName(), 1L, Long::sum);
            if (task.getErrorMessage() != null) {
                data.getGaugesValues().merge(Meters.EXTERNAL_TASKS_OPEN_ERROR.getMeterName(), 1L, Long::sum);
            }

            map.put(tags, data);

        }

        return map.values();
    }
}
