package org.camunda.bpm.run.plugin.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MultiGauge;
import io.micrometer.core.instrument.MultiGauge.Row;
import io.micrometer.core.instrument.Tags;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.ProcessDefinition;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public abstract class Monitor {

    private final ProcessEngine processEngine;
    private final MeterRegistry meterRegistry;
    private final Map<String, MultiGauge> multiGaugeMap = new HashMap<>();


    public Monitor(ProcessEngine processEngine, MeterRegistry meterRegistry) {
        this.processEngine = processEngine;
        this.meterRegistry = meterRegistry;
        initMultiGauges();
    }

    private void initMultiGauges() {
        getGaugeNames().forEach(gaugeName ->
                multiGaugeMap.put(gaugeName, MultiGauge.builder(gaugeName).register(meterRegistry)));
        update();
    }

    protected abstract List<String> getGaugeNames();

    protected abstract Collection<MultiGaugeData> retrieveGaugesData();

    public void update() {
//        Collection<MultiGaugeData> gaugesData = retrieveGaugesData();
//
//        multiGaugeMap.forEach((key, value) -> value.register(
//                gaugesData.stream().map(d -> getRow(key, d)).collect(Collectors.toList()),
//                true));
        Collection<MultiGaugeData> gaugesData = retrieveGaugesData();

        multiGaugeMap.forEach((key, value) -> {
            Iterable<Row<?>> rows = gaugesData.stream()
                    .map(d -> getRow(key, d))
                    .collect(Collectors.toList()); // Здесь возвращаем коллекцию

            value.register(rows, true); // Теперь это будет Iterable
        });

    }

    private Row<MultiGaugeData> getRow(String gaugeName, MultiGaugeData d) {

//        return Row.of(d.getTags(), d, gaugeData -> gaugeData.gaugesValues.get(gaugeName));
        Long value = d.getGaugesValues().get(gaugeName);
        return Row.of(d.getTags(), d, gaugeData -> value != null ? value : 0L);
    }

    protected ProcessDefinition getProcessDefinition(String processDefinitionId) {
        return processEngine.getRepositoryService().createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();
    }

    protected Tags getTagsForProcessDefinition(String processDefinitionId) {
        return addTagsForProcessDefinition(Tags.empty(), processDefinitionId);
    }

    protected Tags addTagsForProcessDefinition(Tags tags, String processDefinitionId) {
        ProcessDefinition pd = getProcessDefinition(processDefinitionId);
        if (pd.getTenantId() != null) {
            tags.and("tenant.id", pd.getTenantId());
        }
        return tags.and("process.definition.id", pd.getId()).and("process.definition.key", pd.getKey());

    }

    @AllArgsConstructor
    @Getter
    protected class MultiGaugeData {
        Map<String, Long> gaugesValues;
        Tags tags;
    }
}
