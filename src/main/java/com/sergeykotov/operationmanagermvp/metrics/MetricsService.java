package com.sergeykotov.operationmanagermvp.metrics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergeykotov.operationmanagermvp.exception.ExtractionException;
import com.sergeykotov.operationmanagermvp.exception.NotFoundException;
import com.sergeykotov.operationmanagermvp.model.Group;
import com.sergeykotov.operationmanagermvp.model.Op;
import com.sergeykotov.operationmanagermvp.repository.GroupRepository;
import com.sergeykotov.operationmanagermvp.repository.OpRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        Metrics metrics = new Metrics();
        //TODO: implement evaluation
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