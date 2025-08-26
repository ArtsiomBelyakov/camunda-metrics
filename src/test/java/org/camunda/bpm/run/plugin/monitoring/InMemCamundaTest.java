package org.camunda.bpm.run.plugin.monitoring;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ProcessEngineExtension.class)
public class InMemCamundaTest {

    ProcessEngine processEngine;

    @Test
    void testDeployment() {
        var runtimeService = processEngine.getRuntimeService();
        assertThat(runtimeService).isNotNull();
    }
}
