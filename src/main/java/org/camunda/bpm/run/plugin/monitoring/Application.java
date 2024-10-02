package org.camunda.bpm.run.plugin.monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableMonitoring
public class Application {

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

}