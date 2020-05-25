package com.sergeykotov.operationmanagermvp.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergeykotov.operationmanagermvp.exception.ExtractionException;
import com.sergeykotov.operationmanagermvp.exception.NotFoundException;
import com.sergeykotov.operationmanagermvp.model.Group;
import com.sergeykotov.operationmanagermvp.repository.GroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MetricsService {
    private static final Logger log = LoggerFactory.getLogger(MetricsService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final GroupRepository groupRepository;

    @Autowired
    public MetricsService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public Metrics evaluate() {
        throw new UnsupportedOperationException(); //TODO: implement MetricsService::evaluate
    }

    public Metrics evaluate(long id) {
        throw new UnsupportedOperationException(); //TODO: implement MetricsService::evaluate by ID
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