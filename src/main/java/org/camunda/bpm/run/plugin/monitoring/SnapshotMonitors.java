package org.camunda.bpm.run.plugin.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.run.plugin.monitoring.externaltask.ExternalTaskSnapshotMonitor;
import org.camunda.bpm.run.plugin.monitoring.incidents.IncidentSnapshotMonitor;
import org.camunda.bpm.run.plugin.monitoring.processinstance.ProcessInstanceSnapshotMonitor;
import org.camunda.bpm.run.plugin.monitoring.task.TaskSnapshotMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SnapshotMonitors {

    private static final Logger log = LoggerFactory.getLogger(SnapshotMonitors.class);
    @Autowired
    ProcessEngine processEngine;

    @Autowired
    MeterRegistry meterRegistry;

    List<Monitor> monitors = new ArrayList<>();

    @PostConstruct
    void init() {
        monitors = List.of(
                new ProcessInstanceSnapshotMonitor(processEngine, meterRegistry),
                new IncidentSnapshotMonitor(processEngine, meterRegistry),
                new TaskSnapshotMonitor(processEngine, meterRegistry),
                new ExternalTaskSnapshotMonitor(processEngine, meterRegistry)
//                ,                new EventSubscriptionMonitoring(processEngine, meterRegistry)

        );
    }

    @Scheduled(fixedDelayString = "${camunda.monitoring.snapshot.updateRate}")
    public void update() {

//        log.info("Updating monitors");
        monitors.forEach(Monitor::update);
    }

}
