package com.github.kayr.javarosa.autofill.api


import org.junit.Before

import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger

trait LogConfig {
    @Before
    void setup() {
        def manager = LogManager.getLogManager()
        manager.readConfiguration(getClass().getResourceAsStream('/logging.properties'))
        def logger = Logger.getLogger("com.github")
        logger.setLevel(Level.FINE)

    }
}
