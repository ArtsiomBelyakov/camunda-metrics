package org.camunda.bpm.run.plugin.monitoring.test;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class FailingDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        throw new RuntimeException("This is a test incident");
    }
} 