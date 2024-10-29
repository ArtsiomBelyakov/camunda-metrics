//package org.camunda.bpm.run.plugin.monitoring.event;
//
//import io.micrometer.core.instrument.Tags;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//
//@AllArgsConstructor
//public enum EventSubscriptionTags {
//
//    TENANT_ID("tenant.id"),
//    PROCESS_INSTANCE_ID("process.instance.id"),
//    EVENT_TYPE("event.type"),
//    EVENT_NAME("event.name"),
//    ACTIVITY_ID("activity.id"),
//    EXECUTION_ID("execution.id");
//
////    TENANT_ID("tenant.id"),
////    PROCESS_DEFINITION_ID("process.definition.id"),
////    PROCESS_DEFINITION_KEY("process.definition.key"),
////    ACTIVITY_ID("activity.id"),
////    EVENT_NAME("event.name"),
////    EVENT_TYPE("event.type");
//
//    @Getter
//    private String tagName;
//
//    public static Tags createTags(String tenantId, String processInstanceId, String eventType, String eventName, String activityId, String executionId) {
////        public static Tags createTags(String tenantId, String processDefinitionId, String processDefinitionKey, String activityId, String eventName, String eventType) {
//        Tags tags = Tags.empty();
//
//        if (tenantId != null) {
//            tags = tags.and(EventSubscriptionTags.TENANT_ID.getTagName(), tenantId);
//        }
//
////            tags = tags.and(EventSubscriptionTags.PROCESS_DEFINITION_ID.getTagName(), processDefinitionId);
////            tags = tags.and(EventSubscriptionTags.PROCESS_DEFINITION_KEY.getTagName(), processDefinitionKey);
////            tags = tags.and(EventSubscriptionTags.ACTIVITY_ID.getTagName(), activityId);
////            tags = tags.and(EventSubscriptionTags.EVENT_NAME.getTagName(), eventName);
////            tags = tags.and(EventSubscriptionTags.EVENT_TYPE.getTagName(), eventType);
//        tags = tags.and(EventSubscriptionTags.PROCESS_INSTANCE_ID.getTagName(), processInstanceId);
//        tags = tags.and(EventSubscriptionTags.EVENT_TYPE.getTagName(), eventType);
//        tags = tags.and(EventSubscriptionTags.EVENT_NAME.getTagName(), activityId);
//        tags = tags.and(EventSubscriptionTags.ACTIVITY_ID.getTagName(), eventName);
//        tags = tags.and(EventSubscriptionTags.EXECUTION_ID.getTagName(), executionId);
//
//        return tags;
//
//    }
//}
