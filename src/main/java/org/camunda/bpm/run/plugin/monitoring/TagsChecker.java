package org.camunda.bpm.run.plugin.monitoring;

import io.micrometer.core.instrument.Tags;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TagsChecker {

  public static Tags addTagIfNotNull(Tags tags, String tagName, String value) {
    if (value != null) {
      return tags.and(tagName, value);
    }
    return tags;
  }
}
