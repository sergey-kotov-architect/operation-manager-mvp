package com.sergeykotov.operationmanagermvp.service;

import com.sergeykotov.operationmanagermvp.event.Event;
import com.sergeykotov.operationmanagermvp.event.EventService;
import com.sergeykotov.operationmanagermvp.exception.DatabaseException;
import com.sergeykotov.operationmanagermvp.exception.ExtractionException;
import com.sergeykotov.operationmanagermvp.model.Executor;
import com.sergeykotov.operationmanagermvp.repository.ExecutorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExecutorService {
    private static final Logger log = LoggerFactory.getLogger(ExecutorService.class);

    private final EventService eventService;
    private final ExecutorRepository executorRepository;

    @Autowired
    public ExecutorService(EventService eventService, ExecutorRepository executorRepository) {
        this.eventService = eventService;
        this.executorRepository = executorRepository;
    }

    public List<Executor> extract() {
        try {
            return executorRepository.extract();
        } catch (Exception e) {
            log.error("failed to extract executors", e);
            throw new ExtractionException();
        }
    }

    public void create(Executor executor) {
        log.info("creating executor {}", executor);
        long start = System.currentTimeMillis();
        try {
            executorRepository.create(executor);
        } catch (Exception e) {
            log.error("failed to create executor {}", executor, e);
            throw new DatabaseException();
        }
        long end = System.currentTimeMillis();
        log.info("executor {} created", executor);
        eventService.create(start, end, Event.Action.CREATED, Event.Entity.EXECUTOR, executor.toString(), executor);
    }

    public void update(long id, Executor executor) {
        executor.setId(id);
        log.info("updating executor {}", executor);
        long start = System.currentTimeMillis();
        try {
            executorRepository.update(executor);
        } catch (Exception e) {
            log.error("failed to update executor {}", executor, e);
            throw new DatabaseException();
        }
        long end = System.currentTimeMillis();
        log.info("executor {} updated", executor);
        eventService.create(start, end, Event.Action.UPDATED, Event.Entity.EXECUTOR, executor.toString(), executor);
    }

    public void delete(long id, Executor executor) {
        executor.setId(id);
        log.info("deleting executor {}", executor);
        long start = System.currentTimeMillis();
        try {
            executorRepository.delete(executor.getId());
        } catch (Exception e) {
            log.error("failed to delete executor {}", executor, e);
            throw new DatabaseException();
        }
        long end = System.currentTimeMillis();
        log.info("executor {} deleted", executor);
        eventService.create(start, end, Event.Action.DELETED, Event.Entity.EXECUTOR, executor.toString(), executor);
    }
}