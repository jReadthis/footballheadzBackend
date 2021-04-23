package com.dmv.footballheadz.util;

import com.dmv.footballheadz.healthcheck.HealthCheckController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HealthCheckControllerTest {

    private HealthCheckController healthCheckController = new HealthCheckController();

    @Test
    public void healthCheckShouldReturnUp() throws Exception {

        String result = healthCheckController.healthCheck();
        assertEquals(result, "up");
    }
}