package org.camunda.bpm.run.plugin.monitoring.incidents;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.Incident;
import org.camunda.bpm.run.plugin.monitoring.Meters;
import org.camunda.bpm.run.plugin.monitoring.Monitor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IncidentSnapshotMonitor extends Monitor {

    public IncidentSnapshotMonitor(ProcessEngine processEngine, MeterRegistry meterRegistry) {
        super(processEngine, meterRegistry);
    }

    @Override
    protected List<String> getGaugeNames() {
        return Stream.of(Meters.INCIDENTS_OPEN, Meters.INCIDENTS_OPEN_NEWEST, Meters.INCIDENTS_OPEN_OLDEST)
                .map(Meters::getMeterName).collect(Collectors.toList());
    }

    @Override
    protected Collection<MultiGaugeData> retrieveGaugesData() {
        Map<String, MultiGaugeData> map = new HashMap<>();
        List<Incident> incs = getProcessEngine().getRuntimeService().createIncidentQuery().unlimitedList();

        Date now = new Date();
//        if (incs.isEmpty()) {
//            // Return a default metric with a value of 0
//            MultiGaugeData defaultData = new MultiGaugeData(
//                    Collections.singletonMap(Meters.INCIDENTS_OPEN.getMeterName(), 0L),
//                    Tags.empty()
//            );
//            return Collections.singletonList(defaultData);
//        }

        for (Incident inc : incs) {
            String groupByKey = inc.getProcessDefinitionId();
            MultiGaugeData data = map.get(groupByKey);

            if (data == null) {
                Map<String, Long> gaugeValues = new HashMap<>();
                gaugeValues.put(Meters.INCIDENTS_OPEN_NEWEST.getMeterName(), Long.MAX_VALUE);
                gaugeValues.put(Meters.INCIDENTS_OPEN_OLDEST.getMeterName(), Long.MIN_VALUE);
                gaugeValues.put(Meters.INCIDENTS_OPEN.getMeterName(), 0L);

                ProcessDefinition processDefinition = getProcessDefinition(inc.getProcessDefinitionId());
                Tags tags = IncidentMeterTags.createTags(processDefinition.getTenantId(), processDefinition.getId(),
                        processDefinition.getKey(), inc.getActivityId(), inc.getFailedActivityId(),
                        inc.getIncidentType());

                data = new MultiGaugeData(gaugeValues, tags);
            }

            long ageSeconds = (now.getTime() - inc.getIncidentTimestamp().getTime()) / 1000;

            data.getGaugesValues().merge(Meters.INCIDENTS_OPEN_NEWEST.getMeterName(), ageSeconds, Long::min);
            data.getGaugesValues().merge(Meters.INCIDENTS_OPEN_OLDEST.getMeterName(), ageSeconds, Long::max);
            data.getGaugesValues().merge(Meters.INCIDENTS_OPEN.getMeterName(), 1L, Long::sum);

            map.put(groupByKey, data);

        }

        return map.values();
    }

}
