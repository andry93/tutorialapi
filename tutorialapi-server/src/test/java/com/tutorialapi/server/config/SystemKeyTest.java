package com.tutorialapi.server.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SystemKeyTest {
    @Test
    public void testDefaultValue() {
        Assertions.assertEquals("8443",SystemKey.PORT.getDefaultValue());
        Assertions.assertEquals("dev",SystemKey.MODE.getDefaultValue());
    }

    @Test
    public void testGetKey() {
        Assertions.assertEquals("port",SystemKey.PORT.getKey());
        Assertions.assertEquals("mode",SystemKey.MODE.getKey());
    }
}
