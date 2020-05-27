package com.sergeykotov.operationmanagermvp.controller;

import com.sergeykotov.operationmanagermvp.metrics.Metrics;
import com.sergeykotov.operationmanagermvp.metrics.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {
    private final MetricsService metricsService;

    @Autowired
    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping
    public Metrics evaluate() {
        return metricsService.evaluate();
    }

    @GetMapping("/{id}")
    public Metrics extractByGroupId(@PathVariable long id) {
        return metricsService.extractByGroupId(id);
    }
}