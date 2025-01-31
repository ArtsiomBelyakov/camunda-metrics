package org.camunda.bpm.run.plugin.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.run.plugin.monitoring.externaltask.ExternalTaskSnapshotMonitor;
import org.camunda.bpm.run.plugin.monitoring.incidents.IncidentSnapshotMonitor;
import org.camunda.bpm.run.plugin.monitoring.processinstance.ProcessInstanceSnapshotMonitor;
import org.camunda.bpm.run.plugin.monitoring.task.TaskSnapshotMonitor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@PropertySource("classpath:library.properties")
@ConditionalOnProperty(value = "camunda.monitoring.snapshot.enabled", havingValue = "true", matchIfMissing = true)
@EnableScheduling
@RequiredArgsConstructor
public class SnapshotMonitors {

//    private static final Logger log = LoggerFactory.getLogger(SnapshotMonitors.class);

    private final ProcessEngine processEngine;
    private final MeterRegistry meterRegistry;

    List<Monitor> monitors = new ArrayList<>();

    @PostConstruct
    void init() {
        monitors = List.of(
                new ProcessInstanceSnapshotMonitor(processEngine, meterRegistry),
                new IncidentSnapshotMonitor(processEngine, meterRegistry),
                new TaskSnapshotMonitor(processEngine, meterRegistry),
                new ExternalTaskSnapshotMonitor(processEngine, meterRegistry)
//              , new EventSubscriptionMonitoring(processEngine, meterRegistry)

        );
    }

    @Scheduled(fixedDelayString = "${camunda.monitoring.snapshot.updateRate}")
    public void update() {
        monitors.forEach(Monitor::update);
    }
}
