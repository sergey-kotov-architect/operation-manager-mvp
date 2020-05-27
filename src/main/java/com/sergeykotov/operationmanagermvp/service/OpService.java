package com.sergeykotov.operationmanagermvp.service;

import com.sergeykotov.operationmanagermvp.event.Event;
import com.sergeykotov.operationmanagermvp.event.EventService;
import com.sergeykotov.operationmanagermvp.exception.DatabaseException;
import com.sergeykotov.operationmanagermvp.exception.ExtractionException;
import com.sergeykotov.operationmanagermvp.exception.NotFoundException;
import com.sergeykotov.operationmanagermvp.metrics.MetricsService;
import com.sergeykotov.operationmanagermvp.model.Op;
import com.sergeykotov.operationmanagermvp.repository.OpRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OpService {
    private static final Logger log = LoggerFactory.getLogger(OpService.class);

    private final EventService eventService;
    private final OpRepository opRepository;
    private final MetricsService metricsService;

    @Autowired
    public OpService(EventService eventService, OpRepository opRepository, MetricsService metricsService) {
        this.eventService = eventService;
        this.opRepository = opRepository;
        this.metricsService = metricsService;
    }

    public List<Op> extractAll() {
        try {
            return opRepository.extractAll();
        } catch (Exception e) {
            log.error("failed to extract ops", e);
            throw new ExtractionException();
        }
    }

    public Op extractById(long id) {
        Optional<Op> op;
        try {
            op = opRepository.extractById(id);
        } catch (Exception e) {
            log.error("failed to extract op by ID: {}", id, e);
            throw new ExtractionException();
        }
        return op.orElseThrow(NotFoundException::new);
    }

    public void create(Op op) {
        log.info("creating op: {}", op);
        long start = System.currentTimeMillis();
        try {
            opRepository.create(op);
        } catch (Exception e) {
            log.error("failed to create op: {}", op, e);
            throw new DatabaseException();
        }
        long end = System.currentTimeMillis();
        log.info("op created: {}", op);
        eventService.create(start, end, Event.Action.CREATED, Event.Entity.OP, op.toString(), op);
        metricsService.createEvaluatingTask(op.getGroup().getId());
    }

    public void updateById(long id, Op op) {
        op.setId(id);
        Op currentOp = extractById(id);
        String update = currentOp.toString();
        if (!update.equals(op.toString())) {
            update = update + " -> " + op.toString();
        }
        log.info("updating op: {}", update);
        long start = System.currentTimeMillis();
        try {
            opRepository.updateById(op);
        } catch (Exception e) {
            log.error("failed to update op: {}", update, e);
            throw new DatabaseException();
        }
        long end = System.currentTimeMillis();
        log.info("op updated: {}", update);
        eventService.create(start, end, Event.Action.UPDATED, Event.Entity.OP, op.toString(), op);
        metricsService.createEvaluatingTask(op.getGroup().getId());
    }

    public void deleteById(long id) {
        Op op = extractById(id);
        log.info("deleting op: {}", op);
        long start = System.currentTimeMillis();
        try {
            opRepository.deleteById(id);
        } catch (Exception e) {
            log.error("failed to delete op: {}", op, e);
            throw new DatabaseException();
        }
        long end = System.currentTimeMillis();
        log.info("op deleted: {}", op);
        eventService.create(start, end, Event.Action.DELETED, Event.Entity.OP, op.toString(), op);
        metricsService.createEvaluatingTask(op.getGroup().getId());
    }
}