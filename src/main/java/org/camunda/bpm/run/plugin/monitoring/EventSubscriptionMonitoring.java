package org.camunda.bpm.run.plugin.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.event.EventType;
import org.camunda.bpm.engine.runtime.EventSubscription;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventSubscriptionMonitoring extends Monitor {

    public EventSubscriptionMonitoring(ProcessEngine processEngine, MeterRegistry meterRegistry) {
        super(processEngine, meterRegistry);
    }

    @Override
    protected List<String> getGaugeNames() {
//        return Arrays.asList(Meters.MESSAGE_EVENT_SUBSCRIPTIONS_ACTIVE,
//                             Meters.SIGNAL_EVENT_SUBSCRIPTIONS_ACTIVE,
//                             Meters.COMPENSATE_EVENT_SUBSCRIPTIONS_ACTIVE,
//                             Meters.CONDITIONAL_EVENT_SUBSCRIPTIONS_ACTIVE)
        return Stream.of(Meters.MESSAGE,
                        Meters.SIGNAL,
                        Meters.COMPENSATE,
                        Meters.CONDITIONAL)
                .map(Meters::getMeterName)
                .collect(Collectors.toList());
    }

//    @Override
//    protected Collection<MultiGaugeData> retrieveGaugesData() {
//        Map<String, MultiGaugeData> map = new HashMap<>();
////        Map<Tags, MultiGaugeData> map = new HashMap<>();
//
//
//        List<EventSubscription> eventSubscriptions = getProcessEngine().getRuntimeService().createEventSubscriptionQuery().unlimitedList();
//
//        for (EventSubscription eventSubscription : eventSubscriptions) {
//            String groupByKey = eventSubscription.getProcessInstanceId();
//
//
////            String groupByKey = eventSubscription.getEventType();
////            MultiGaugeData data = map.get(groupByKey);
//            MultiGaugeData data = map.get(eventSubscription.getProcessInstanceId());
//
//            if (data == null) {
//                Map<String, Long> gaugeValues = new HashMap<>();
//                gaugeValues.put(Meters.MESSAGE.getMeterName(), Long.valueOf(0));
//                gaugeValues.put(Meters.SIGNAL.getMeterName(), Long.valueOf(0));
//                gaugeValues.put(Meters.COMPENSATE.getMeterName(), Long.valueOf(0));
//                gaugeValues.put(Meters.CONDITIONAL.getMeterName(), Long.valueOf(0));
//
//                Tags tags = EventSubscriptionTags.createTags(eventSubscription.getTenantId(), eventSubscription.getProcessInstanceId(),
//                        eventSubscription.getEventType(), eventSubscription.getEventName(), eventSubscription.getActivityId(), eventSubscription.getExecutionId());
//
//                data = new MultiGaugeData(gaugeValues, tags);
//            }
//
//
////            switch (eventSubscription.getEventType()) {
////                case ("message"):
////                    data.gaugesValues.merge(Meters.MESSAGE_EVENT_SUBSCRIPTIONS_ACTIVE.getMeterName(), Long.valueOf(1), Long::sum);
////                    break;
////                case ("signal"):
////                    data.gaugesValues.merge(Meters.SIGNAL_EVENT_SUBSCRIPTIONS_ACTIVE.getMeterName(), Long.valueOf(1), Long::sum);
////                    break;
////                case ("compensate"):
////                    data.gaugesValues.merge(Meters.COMPENSATE_EVENT_SUBSCRIPTIONS_ACTIVE.getMeterName(), Long.valueOf(1), Long::sum);
////                    break;
////                case ("conditional"):
////                    data.gaugesValues.merge(Meters.CONDITIONAL_EVENT_SUBSCRIPTIONS_ACTIVE.getMeterName(), Long.valueOf(1), Long::sum);
////                    break;
////            }
//
////            data.gaugesValues.merge(Meters.MESSAGE_EVENT_SUBSCRIPTIONS_ACTIVE.getMeterName(), Long.valueOf(1), Long::sum);
////            data.gaugesValues.merge(Meters.SIGNAL_EVENT_SUBSCRIPTIONS_ACTIVE.getMeterName(), Long.valueOf(1), Long::sum);
////            data.gaugesValues.merge(Meters.COMPENSATE_EVENT_SUBSCRIPTIONS_ACTIVE.getMeterName(), Long.valueOf(1), Long::sum);
////            data.gaugesValues.merge(Meters.CONDITIONAL_EVENT_SUBSCRIPTIONS_ACTIVE.getMeterName(), Long.valueOf(1), Long::sum);
//
//            map.put(groupByKey, data);
//
//        }
//
//        return map.values();
//    }


    @Override
    protected Collection<MultiGaugeData> retrieveGaugesData() {
//        Map<String, MultiGaugeData> map = new HashMap<>();
        Map<Tags, MultiGaugeData> map = new HashMap<>();


        List<EventSubscription> eventSubscriptions = getProcessEngine().getRuntimeService().createEventSubscriptionQuery().unlimitedList();

        for (EventSubscription eventSubscription : eventSubscriptions) {
            Tags tags = EventSubscriptionTags.createTags(eventSubscription.getTenantId(), eventSubscription.getProcessInstanceId(),
                    eventSubscription.getEventType(), eventSubscription.getEventName(), eventSubscription.getActivityId(), eventSubscription.getExecutionId());


//            String groupByKey = eventSubscription.getProcessInstanceId();
//            MultiGaugeData data = map.get(groupByKey);
            MultiGaugeData data = map.get(tags);

            Map<String, Long> gaugeValues = new HashMap<>();
            switch (eventSubscription.getEventType()) {
                case "message":
                    if (data == null) {
                        gaugeValues.put(Meters.MESSAGE.getMeterName(), Long.valueOf(0));
                        data = new MultiGaugeData(gaugeValues, tags);
                    }
                    break;
                case "signal":
                    if (data == null) {
                        gaugeValues.put(Meters.SIGNAL.getMeterName(), Long.valueOf(0));
                        data = new MultiGaugeData(gaugeValues, tags);
                    }
                    break;
                case "compensate":
                    if (data == null) {
                        gaugeValues.put(Meters.COMPENSATE.getMeterName(), Long.valueOf(0));
                        data = new MultiGaugeData(gaugeValues, tags);
                    }
                    break;
                case "conditional":
                    if (data == null) {
                        gaugeValues.put(Meters.CONDITIONAL.getMeterName(), Long.valueOf(0));
                        data = new MultiGaugeData(gaugeValues, tags);
                    }
                    break;
            }


            map.put(tags, data);

            switch (eventSubscription.getEventType()) {
                case ("message"):
                    data.gaugesValues.merge(Meters.MESSAGE.getMeterName(), Long.valueOf(1), Long::sum);
                    break;
                case ("signal"):
                    data.gaugesValues.merge(Meters.SIGNAL.getMeterName(), Long.valueOf(1), Long::sum);
                    break;
                case ("compensate"):
                    data.gaugesValues.merge(Meters.COMPENSATE.getMeterName(), Long.valueOf(1), Long::sum);
                    break;
                case ("conditional"):
                    data.gaugesValues.merge(Meters.CONDITIONAL.getMeterName(), Long.valueOf(1), Long::sum);
                    break;
            }
        }

        return map.values();
    }
}
