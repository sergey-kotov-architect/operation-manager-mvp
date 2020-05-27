package com.sergeykotov.operationmanagermvp.metrics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergeykotov.operationmanagermvp.exception.ExtractionException;
import com.sergeykotov.operationmanagermvp.exception.NotFoundException;
import com.sergeykotov.operationmanagermvp.model.*;
import com.sergeykotov.operationmanagermvp.repository.GroupRepository;
import com.sergeykotov.operationmanagermvp.repository.OpRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class MetricsService {
    private static final Logger log = LoggerFactory.getLogger(MetricsService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int PROCESS_COUNT = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService executorService = Executors.newFixedThreadPool(PROCESS_COUNT - 1);

    private final GroupRepository groupRepository;
    private final OpRepository opRepository;

    @Autowired
    public MetricsService(GroupRepository groupRepository, OpRepository opRepository) {
        this.groupRepository = groupRepository;
        this.opRepository = opRepository;
    }

    public Metrics evaluate() {
        try {
            List<Op> ops = opRepository.extractSchedule();
            return evaluate(ops);
        } catch (Exception e) {
            log.error("failed to extract schedule", e);
            throw new ExtractionException();
        }
    }

    public void createEvaluatingTask(long id) {
        EvaluatingTask evaluatingTask = new EvaluatingTask(id, this);
        executorService.submit(evaluatingTask);
        log.info("evaluating task created for group ID {}", id);
    }

    public Metrics evaluate(long id) throws SQLException {
        List<Op> ops = opRepository.extractSchedule(id);
        return evaluate(ops);
    }

    private Metrics evaluate(List<Op> ops) {
        Set<Task> tasks = ops.stream().map(Op::getTask).collect(Collectors.toSet());
        Set<Executor> executors = ops.stream().map(Op::getExecutor).collect(Collectors.toSet());
        Set<Period> periods = ops.stream().map(Op::getPeriod).collect(Collectors.toSet());
        Set<Group> groups = ops.stream().map(Op::getGroup).collect(Collectors.toSet());

        long start = periods.stream().mapToLong(Period::getStart).min().orElse(0L);
        long end = periods.stream().mapToLong(Period::getEnd).max().orElse(0L);

        double mean = ops.stream().mapToDouble(Op::getCost).sum() / executors.size();
        double deviation = 0.0;
        for (Executor executor : executors) {
            double cost = ops.stream().filter(o -> o.getExecutor().equals(executor)).mapToDouble(Op::getCost).sum();
            deviation += Math.abs(cost - mean);
        }
        deviation = deviation / executors.size();

        Metrics metrics = new Metrics();
        metrics.setOpCount(ops.size());
        metrics.setTaskCount(tasks.size());
        metrics.setExecutorCount(executors.size());
        metrics.setPeriodCount(periods.size());
        metrics.setGroupCount(groups.size());
        metrics.setStart(start);
        metrics.setEnd(end);
        metrics.setMeanDeviation(deviation);
        return metrics;
    }

    public void save(long id, Metrics metrics) throws JsonProcessingException, SQLException {
        String json = objectMapper.writeValueAsString(metrics);
        groupRepository.updateMetricsById(id, json);
    }

    public Metrics extract(long id) {
        Optional<Group> group;
        try {
            group = groupRepository.findById(id);
        } catch (Exception e) {
            log.error("failed to extract metrics for group ID {}", id, e);
            throw new ExtractionException();
        }
        String json = group.orElseThrow(NotFoundException::new).getMetrics();
        Metrics metrics;
        try {
            metrics = objectMapper.readValue(json, Metrics.class);
        } catch (Exception e) {
            log.error("failed to parse JSON metrics: {}", json, e);
            throw new ExtractionException();
        }
        return metrics;
    }
}