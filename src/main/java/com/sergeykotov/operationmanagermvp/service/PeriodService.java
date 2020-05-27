package com.sergeykotov.operationmanagermvp.service;

import com.sergeykotov.operationmanagermvp.event.Event;
import com.sergeykotov.operationmanagermvp.event.EventService;
import com.sergeykotov.operationmanagermvp.exception.DatabaseException;
import com.sergeykotov.operationmanagermvp.exception.ExtractionException;
import com.sergeykotov.operationmanagermvp.exception.NotFoundException;
import com.sergeykotov.operationmanagermvp.model.Period;
import com.sergeykotov.operationmanagermvp.repository.PeriodRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public List<Period> extractAll() {
        try {
            return periodRepository.extractAll();
        } catch (Exception e) {
            log.error("failed to extract periods", e);
            throw new ExtractionException();
        }
    }

    public Period extractById(long id) {
        Optional<Period> period;
        try {
            period = periodRepository.extractById(id);
        } catch (Exception e) {
            log.error("failed to extract period by ID: {}", id, e);
            throw new ExtractionException();
        }
        return period.orElseThrow(NotFoundException::new);
    }

    public void create(Period period) {
        log.info("creating period: {}", period);
        long start = System.currentTimeMillis();
        try {
            periodRepository.create(period);
        } catch (Exception e) {
            log.error("failed to create period: {}", period, e);
            throw new DatabaseException();
        }
        long end = System.currentTimeMillis();
        log.info("period created: {}", period);
        eventService.create(start, end, Event.Action.CREATED, Event.Entity.PERIOD, period.toString(), period);
    }

    public void updateById(long id, Period period) {
        period.setId(id);
        Period currentPeriod = extractById(id);
        String update = currentPeriod.getName();
        if (!update.equals(period.getName())) {
            update = update + " -> " + period.getName();
        }
        log.info("updating period: {}", update);
        long start = System.currentTimeMillis();
        try {
            periodRepository.updateById(period);
        } catch (Exception e) {
            log.error("failed to update period: {}", update, e);
            throw new DatabaseException();
        }
        long end = System.currentTimeMillis();
        log.info("period updated: {}", update);
        eventService.create(start, end, Event.Action.UPDATED, Event.Entity.PERIOD, period.toString(), period);
    }

    public void deleteById(long id) {
        Period period = extractById(id);
        log.info("deleting period: {}", period);
        long start = System.currentTimeMillis();
        try {
            periodRepository.deleteById(id);
        } catch (Exception e) {
            log.error("failed to delete period: {}", period, e);
            throw new DatabaseException();
        }
        long end = System.currentTimeMillis();
        log.info("period deleted: {}", period);
        eventService.create(start, end, Event.Action.DELETED, Event.Entity.PERIOD, period.toString(), period);
    }
}