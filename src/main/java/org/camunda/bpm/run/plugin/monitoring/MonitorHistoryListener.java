package org.camunda.bpm.run.plugin.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.camunda.bpm.engine.impl.history.event.HistoricIncidentEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoricProcessInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoryEventTypes;
import org.camunda.bpm.run.plugin.monitoring.incidents.IncidentMeterTags;
import org.camunda.bpm.run.plugin.monitoring.processinstance.ProcessInstanceMeterTags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MonitorHistoryListener {

  TaggedCounter processInstancesStartedCounter;
  TaggedCounter processInstancesEndedCounter;
  TaggedCounter incidentsCreatedCounter;
  TaggedCounter incidentsResolvedCounter;
  TaggedCounter incidentsDeletedCounter;

  @Autowired
  public MonitorHistoryListener(MeterRegistry meterRegistry) {
    processInstancesStartedCounter = new TaggedCounter(
        Meters.PROCESS_INSTANCES_STARTED.getMeterName(), meterRegistry);
    processInstancesEndedCounter = new TaggedCounter(Meters.PROCESS_INSTANCES_ENDED.getMeterName(),
        meterRegistry);
    incidentsCreatedCounter = new TaggedCounter(Meters.INCIDENTS_CREATED.getMeterName(),
        meterRegistry);
    incidentsResolvedCounter = new TaggedCounter(Meters.INCIDENTS_RESOLVED.getMeterName(),
        meterRegistry);
    incidentsDeletedCounter = new TaggedCounter(Meters.INCIDENTS_DELETED.getMeterName(),
        meterRegistry);
  }

  @EventListener
  public void onHistoricProcessInstanceEvent(HistoricProcessInstanceEventEntity historyEvent) {
    Tags tags = createProcessInstanceTags(historyEvent);

    if (historyEvent.isEventOfType(HistoryEventTypes.PROCESS_INSTANCE_START)) {
      processInstancesStartedCounter.increment(tags);
    } else if (historyEvent.isEventOfType(HistoryEventTypes.PROCESS_INSTANCE_END)) {
      processInstancesEndedCounter.increment(tags);
    }
  }

  private Tags createProcessInstanceTags(HistoricProcessInstanceEventEntity historyEvent) {
    return ProcessInstanceMeterTags.createTags(
        historyEvent.getTenantId(),
        historyEvent.getProcessDefinitionKey()
    );
  }

  @EventListener
  public void onHistoricIncidentEvent(HistoricIncidentEventEntity historyEvent) {
    Tags tags = createIncidentTags(historyEvent);

    if (historyEvent.isEventOfType(HistoryEventTypes.INCIDENT_CREATE)) {
      incidentsCreatedCounter.increment(tags);
    } else if (historyEvent.isEventOfType(HistoryEventTypes.INCIDENT_DELETE)) {
      incidentsDeletedCounter.increment(tags);
    } else if (historyEvent.isEventOfType(HistoryEventTypes.INCIDENT_RESOLVE)) {
      incidentsResolvedCounter.increment(tags);
    }
  }

  private Tags createIncidentTags(HistoricIncidentEventEntity historyEvent) {
    return IncidentMeterTags.createTags(
        historyEvent.getTenantId(),
        historyEvent.getProcessDefinitionKey(),
        historyEvent.getActivityId(),
        historyEvent.getFailedActivityId(),
        historyEvent.getIncidentType()
    );
  }
}
