package com.sergeykotov.operationmanagermvp.service;

import com.sergeykotov.operationmanagermvp.event.Event;
import com.sergeykotov.operationmanagermvp.event.EventService;
import com.sergeykotov.operationmanagermvp.exception.DatabaseException;
import com.sergeykotov.operationmanagermvp.exception.ExtractionException;
import com.sergeykotov.operationmanagermvp.model.Op;
import com.sergeykotov.operationmanagermvp.repository.OpRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpService {
    private static final Logger log = LoggerFactory.getLogger(OpService.class);

    private final EventService eventService;
    private final OpRepository opRepository;

    @Autowired
    public OpService(EventService eventService, OpRepository opRepository) {
        this.eventService = eventService;
        this.opRepository = opRepository;
    }

    public List<Op> extract() {
        try {
            return opRepository.extract();
        } catch (Exception e) {
            log.error("failed to extract ops", e);
            throw new ExtractionException();
        }
    }

    public void create(Op op) {
        log.info("creating op {}", op);
        long start = System.currentTimeMillis();
        try {
            opRepository.create(op);
        } catch (Exception e) {
            log.error("failed to create op {}", op, e);
            throw new DatabaseException();
        }
        long end = System.currentTimeMillis();
        log.info("op {} created", op);
        eventService.create(start, end, Event.Action.CREATED, Event.Entity.OP, op.toString(), op);
    }

    public void update(Op op) {
        log.info("updating op {}", op);
        long start = System.currentTimeMillis();
        try {
            opRepository.update(op);
        } catch (Exception e) {
            log.error("failed to update op {}", op, e);
            throw new DatabaseException();
        }
        long end = System.currentTimeMillis();
        log.info("op {} updated", op);
        eventService.create(start, end, Event.Action.UPDATED, Event.Entity.OP, op.toString(), op);
    }

    public void delete(Op op) {
        log.info("deleting op {}", op);
        long start = System.currentTimeMillis();
        try {
            opRepository.delete(op.getId());
        } catch (Exception e) {
            log.error("failed to delete op {}", op, e);
            throw new DatabaseException();
        }
        long end = System.currentTimeMillis();
        log.info("op {} deleted", op);
        eventService.create(start, end, Event.Action.DELETED, Event.Entity.OP, op.toString(), op);
    }
}