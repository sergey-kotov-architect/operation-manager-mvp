package com.sergeykotov.operationmanagermvp.schedule;

import com.sergeykotov.operationmanagermvp.event.Event;
import com.sergeykotov.operationmanagermvp.event.EventService;
import com.sergeykotov.operationmanagermvp.exception.ExtractionException;
import com.sergeykotov.operationmanagermvp.model.Op;
import com.sergeykotov.operationmanagermvp.repository.OpRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ScheduleService {
    private static final Logger log = LoggerFactory.getLogger(ScheduleService.class);
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final OpRepository opRepository;
    private final OptimisationService optimisationService;
    private final EventService eventService;

    @Autowired
    public ScheduleService(OpRepository opRepository, OptimisationService optimisationService, EventService eventService) {
        this.opRepository = opRepository;
        this.optimisationService = optimisationService;
        this.eventService = eventService;
    }

    public Schedule extract() {
        List<Op> ops;
        try {
            ops = opRepository.extractSchedule();
        } catch (Exception e) {
            log.error("failed to extract schedule", e);
            throw new ExtractionException();
        }
        return new Schedule(-1, ops);
    }

    public Schedule extract(long groupId) {
        List<Op> ops;
        try {
            ops = opRepository.extractSchedule(groupId);
        } catch (Exception e) {
            log.error("failed to extract schedule for group ID {}", groupId, e);
            throw new ExtractionException();
        }
        return new Schedule(groupId, ops);
    }

    public void createSchedulingTask(long groupId) {
        List<Op> ops;
        try {
            ops = opRepository.extract(groupId);
        } catch (Exception e) {
            log.error("failed to extract ops for group ID {}", groupId, e);
            throw new ExtractionException();
        }
        long start = System.currentTimeMillis();
        SchedulingTask schedulingTask = new SchedulingTask(this, optimisationService, eventService, ops, groupId, start);
        executorService.submit(schedulingTask);
        log.info("scheduling task created for group ID {}", groupId);
    }

    public void save(Schedule schedule) {
        long start = System.currentTimeMillis();
        log.info("saving schedule {}", schedule);
        try {
            opRepository.updateStatus(schedule.getOps());
        } catch (Exception e) {
            long end = System.currentTimeMillis();
            log.error("failed to save schedule {}", schedule, e);
            eventService.create(start, end, Event.Action.FAILED_TO_SAVE, Event.Entity.SCHEDULE, schedule.toString(), schedule);
            return;
        }
        long end = System.currentTimeMillis();
        log.info("schedule {} saved", schedule);
        eventService.create(start, end, Event.Action.SAVED, Event.Entity.SCHEDULE, schedule.toString(), schedule);
    }
}