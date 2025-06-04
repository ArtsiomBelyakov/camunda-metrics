package org.camunda.bpm.run.plugin.monitoring;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TagsCheckerTest {
    @Test
    void testNonNull() {
        // arrange
        String input = "non-null-input";
        String defaultOutput = "default-output";

        // act
        String methodRes = TagsChecker.checkNull(input, defaultOutput);

        // assert
        assertEquals(input, methodRes);
    }

    @Test
    void testNonNullInherited() {
        // arrange
        Number number = null;
        Double defaultOutput = 52.0;

        // act
        Number methodRes = TagsChecker.checkNull(number, defaultOutput);

        // assert
        assertEquals(defaultOutput, methodRes);
    }

    @Test
    void testNullInput() {
        // arrange
        String input = null;
        String defaultOutput = "default-output";

        // act
        String methodRes = TagsChecker.checkNull(input, defaultOutput);

        // assert
        assertEquals(defaultOutput, methodRes);
    }
}
