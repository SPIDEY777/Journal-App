package com.journ.JournalApp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthCheck {

    @GetMapping("/health-check")
    public Map<String, Object> healthCheck() {
        long uptimeMs = ManagementFactory.getRuntimeMXBean().getUptime();
        Duration duration = Duration.ofMillis(uptimeMs);

        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        String formattedUptime = String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("uptime", formattedUptime);
        response.put("uptimeSeconds", duration.getSeconds());

        return response;
    }
}
