package org.camunda.bpm.run.plugin.monitoring.task;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.run.plugin.monitoring.Meters;
import org.camunda.bpm.run.plugin.monitoring.Monitor;

import java.util.*;
import java.util.stream.Collectors;

public class TaskSnapshotMonitor extends Monitor {

    public TaskSnapshotMonitor(ProcessEngine processEngine, MeterRegistry meterRegistry) {
        super(processEngine, meterRegistry);
    }

    @Override
    protected List<String> getGaugeNames() {
        return Arrays.asList(Meters.TASKS_OPEN, Meters.TASKS_OPEN_NEWEST, Meters.TASKS_OPEN_OLDEST).stream()
                .map(Meters::getMeterName).collect(Collectors.toList());
    }

    @Override
    protected Collection<MultiGaugeData> retrieveGaugesData() {
        Map<Tags, MultiGaugeData> map = new HashMap<>();
        List<Task> tasks = getProcessEngine().getTaskService().createTaskQuery().unlimitedList();

        Date now = new Date();

        for (Task task : tasks) {
            Tags tags;
            if (task.getProcessInstanceId() != null) {
                // Task is related to a process instance
                ProcessDefinition processDefinition = getProcessDefinition(task.getProcessDefinitionId());
                tags = TaskProcessInstanceMeterTags.createTags(task.getTenantId(), task.getProcessDefinitionId(),
                        processDefinition.getKey(), task.getTaskDefinitionKey());

            } else if (task.getCaseInstanceId() != null) {
                // Task is related to a case instance
                tags = TaskCaseInstanceMeterTags.createTags(task.getTenantId(), task.getCaseDefinitionId(),
                        task.getTaskDefinitionKey());

            } else {
                // Task is stand-alone
                tags = TaskStandAloneMeterTags.createTags(task.getTenantId(), task.getName());
            }

            MultiGaugeData data = map.get(tags);

            if (data == null) {
                Map<String, Long> gaugeValues = new HashMap<>();
                gaugeValues.put(Meters.TASKS_OPEN_NEWEST.getMeterName(), Long.MAX_VALUE);
                gaugeValues.put(Meters.TASKS_OPEN_OLDEST.getMeterName(), Long.MIN_VALUE);
                gaugeValues.put(Meters.TASKS_OPEN.getMeterName(), 0L);

                data = new MultiGaugeData(gaugeValues, tags);
            }

            long ageSeconds = (now.getTime() - task.getCreateTime().getTime()) / 1000;

            data.getGaugesValues().merge(Meters.TASKS_OPEN_NEWEST.getMeterName(), ageSeconds, Long::min);
            data.getGaugesValues().merge(Meters.TASKS_OPEN_OLDEST.getMeterName(), ageSeconds, Long::max);
            data.getGaugesValues().merge(Meters.TASKS_OPEN.getMeterName(), 1L, Long::sum);

            map.put(tags, data);

        }

        return map.values();
    }

}
