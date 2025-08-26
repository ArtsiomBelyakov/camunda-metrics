package org.camunda.bpm.run.plugin.monitoring;

import java.util.Objects;

public final class TagsChecker {
    private TagsChecker() {
    }

    public static <I, O extends I> I checkNull(I o, O defaultValue) {
        return Objects.requireNonNullElse(o, defaultValue);
    }
}
