package org.camunda.bpm.run.plugin.monitoring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Documented
@Import({ MonitorHistoryListener.class, SnapshotMonitors.class })
@Configuration
public @interface EnableMonitoring {

}
