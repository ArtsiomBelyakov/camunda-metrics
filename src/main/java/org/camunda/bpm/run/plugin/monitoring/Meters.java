package org.camunda.bpm.run.plugin.monitoring;

import lombok.Getter;

/**
 * Meters to expose
 */
@Getter
public enum Meters {

    PROCESS_INSTANCES_STARTED("camunda.process.instances.started"),
    PROCESS_INSTANCES_ENDED("camunda.process.instances.ended"),
    PROCESS_INSTANCES_RUNNING("camunda.process.instances.running.total"),
    PROCESS_INSTANCES_SUSPENDED("camunda.process.instances.running.suspended.total"),
    INCIDENTS_CREATED("camunda.incidents.created"), INCIDENTS_DELETED("camunda.incidents.deleted"),
    INCIDENTS_RESOLVED("camunda.incidents.resolved"), INCIDENTS_OPEN("camunda.incidents.open.total"),
    INCIDENTS_OPEN_NEWEST("camunda.incidents.open.age.newest.seconds"),
    INCIDENTS_OPEN_OLDEST("camunda.incidents.open.age.oldest.seconds"), TASKS_CREATED("camunda.tasks.created"),
    TASKS_COMPLETED("camunda.tasks.completed"), TASKS_DELETED("camunda.tasks.deleted"),
    TASKS_OPEN("camunda.tasks.open.total"), TASKS_OPEN_NEWEST("camunda.tasks.open.age.newest.seconds"),
    TASKS_OPEN_OLDEST("camunda.tasks.open.age.oldest.seconds"),
    EXTERNAL_TASKS_STARTED("camunda.external.tasks.started"), EXTERNAL_TASKS_ENDED("camunda.external.tasks.ended"),
    EXTERNAL_TASKS_OPEN("camunda.external.tasks.open.total"),
    EXTERNAL_TASKS_OPEN_ERROR("camunda.external.tasks.open.error.total"),


    MESSAGE("camunda.message.event.subscriptions.active"),
    SIGNAL("camunda.signal.event.subscriptions.active"),
    COMPENSATE("camunda.compensate.event.subscriptions.active"),
    CONDITIONAL("camunda.conditional.event.subscriptions.active");
//    MESSAGE_EVENT_SUBSCRIPTIONS_ACTIVE("camunda.message.event.subscriptions.active"),
//    SIGNAL_EVENT_SUBSCRIPTIONS_ACTIVE("camunda.signal.event.subscriptions.active"),
//    COMPENSATE_EVENT_SUBSCRIPTIONS_ACTIVE("camunda.compensate.event.subscriptions.active"),
//    CONDITIONAL_EVENT_SUBSCRIPTIONS_ACTIVE("camunda.conditional.event.subscriptions.active");

    Meters(String meterName) {
        this.meterName = meterName;
    }

    private final String meterName;

}
