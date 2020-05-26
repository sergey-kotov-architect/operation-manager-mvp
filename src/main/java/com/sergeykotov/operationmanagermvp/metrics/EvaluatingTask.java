package com.sergeykotov.operationmanagermvp.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvaluatingTask extends Thread {
    private static final Logger log = LoggerFactory.getLogger(EvaluatingTask.class);

    private final long id;
    private final MetricsService metricsService;

    public EvaluatingTask(long id, MetricsService metricsService) {
        this.id = id;
        this.metricsService = metricsService;
        setName("evaluating-task-" + id);
    }

    @Override
    public void run() {
        log.info("evaluating initiated for group ID {}", id);
        try {
            Metrics metrics = metricsService.evaluate(id);
            metricsService.save(id, metrics);
        } catch (Exception e) {
            log.error("failed to evaluate metrics for group ID {}", id, e);
            return;
        }
        log.info("evaluating completed for group ID {}", id);
    }
}