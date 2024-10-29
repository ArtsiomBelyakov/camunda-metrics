package org.camunda.bpm.run.plugin.monitoring.processinstance;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.run.plugin.monitoring.Meters;
import org.camunda.bpm.run.plugin.monitoring.Monitor;

import java.util.*;
import java.util.stream.Collectors;

public class ProcessInstanceSnapshotMonitor extends Monitor {

    public ProcessInstanceSnapshotMonitor(ProcessEngine processEngine, MeterRegistry meterRegistry) {
        super(processEngine, meterRegistry);
    }

    @Override
    protected List<String> getGaugeNames() {
        return Arrays.asList(
                Meters.PROCESS_INSTANCES_RUNNING.getMeterName(),
                Meters.PROCESS_INSTANCES_SUSPENDED.getMeterName()
        );
    }

    @Override
    protected Collection<MultiGaugeData> retrieveGaugesData() {
        Map<String, MultiGaugeData> map = new HashMap<>();
        List<ProcessInstance> processInstances = getProcessEngine()
                .getRuntimeService()
                .createProcessInstanceQuery()
                .unlimitedList();

        for (ProcessInstance pi : processInstances) {
            String groupByKey = pi.getProcessDefinitionId();
            MultiGaugeData data = map.computeIfAbsent(groupByKey, key -> {
                Map<String, Long> gaugeValues = new HashMap<>();
                gaugeValues.put(Meters.PROCESS_INSTANCES_RUNNING.getMeterName(), 0L);
                gaugeValues.put(Meters.PROCESS_INSTANCES_SUSPENDED.getMeterName(), 0L);

                ProcessDefinition processDefinition = getProcessDefinition(pi.getProcessDefinitionId());
                Tags tags = ProcessInstanceMeterTags.createTags(pi.getTenantId(), pi.getProcessDefinitionId(), processDefinition.getKey());
                return new MultiGaugeData(gaugeValues, tags);
            });

            data.getGaugesValues().merge(Meters.PROCESS_INSTANCES_RUNNING.getMeterName(), 1L, Long::sum);
            if (pi.isSuspended()) {
                data.getGaugesValues().merge(Meters.PROCESS_INSTANCES_SUSPENDED.getMeterName(), 1L, Long::sum);
            }
        }

        return map.values();
    }

}
