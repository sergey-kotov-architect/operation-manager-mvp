package com.sergeykotov.operationmanagermvp.service;

import com.sergeykotov.operationmanagermvp.event.Event;
import com.sergeykotov.operationmanagermvp.event.EventService;
import com.sergeykotov.operationmanagermvp.exception.DatabaseException;
import com.sergeykotov.operationmanagermvp.exception.ExtractionException;
import com.sergeykotov.operationmanagermvp.model.Period;
import com.sergeykotov.operationmanagermvp.repository.PeriodRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PeriodService {
    private static final Logger log = LoggerFactory.getLogger(PeriodService.class);

    private final EventService eventService;
    private final PeriodRepository periodRepository;

    @Autowired
    public PeriodService(EventService eventService, PeriodRepository periodRepository) {
        this.eventService = eventService;
        this.periodRepository = periodRepository;
    }

    public List<Period> extract() {
        try {
            return periodRepository.extract();
        } catch (Exception e) {
            log.error("failed to extract periods", e);
            throw new ExtractionException();
        }
    }

    public void create(Period period) {
        log.info("creating period {}", period);
        long start = System.currentTimeMillis();
        try {
            periodRepository.create(period);
        } catch (Exception e) {
            log.error("failed to create period {}", period, e);
            throw new DatabaseException();
        }
        long end = System.currentTimeMillis();
        log.info("period {} created", period);
        eventService.create(start, end, Event.Action.CREATED, Event.Entity.PERIOD, period.toString(), period);
    }

    public void update(long id, Period period) {
        period.setId(id);
        log.info("updating period {}", period);
        long start = System.currentTimeMillis();
        try {
            periodRepository.update(period);
        } catch (Exception e) {
            log.error("failed to update period {}", period, e);
            throw new DatabaseException();
        }
        long end = System.currentTimeMillis();
        log.info("period {} updated", period);
        eventService.create(start, end, Event.Action.UPDATED, Event.Entity.PERIOD, period.toString(), period);
    }

    public void delete(long id, Period period) {
        period.setId(id);
        log.info("deleting period {}", period);
        long start = System.currentTimeMillis();
        try {
            periodRepository.delete(period.getId());
        } catch (Exception e) {
            log.error("failed to delete period {}", period, e);
            throw new DatabaseException();
        }
        long end = System.currentTimeMillis();
        log.info("period {} deleted", period);
        eventService.create(start, end, Event.Action.DELETED, Event.Entity.PERIOD, period.toString(), period);
    }
}