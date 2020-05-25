package com.sergeykotov.operationmanagermvp.schedule;

import com.sergeykotov.operationmanagermvp.event.Event;
import com.sergeykotov.operationmanagermvp.event.EventService;
import com.sergeykotov.operationmanagermvp.model.Op;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SchedulingTask extends Thread {
    private static final Logger log = LoggerFactory.getLogger(SchedulingTask.class);

    private final ScheduleService scheduleService;
    private final OptimisationService optimisationService;
    private final EventService eventService;
    private final List<Op> ops;
    private final long groupId;
    private final long creationTime;

    public SchedulingTask(ScheduleService scheduleService,
                          OptimisationService optimisationService,
                          EventService eventService,
                          List<Op> ops,
                          long groupId,
                          long creationTime) {
        this.scheduleService = scheduleService;
        this.optimisationService = optimisationService;
        this.eventService = eventService;
        this.ops = ops;
        this.groupId = groupId;
        this.creationTime = creationTime;
        setName("scheduling-" + groupId);
    }

    @Override
    public void run() {
        log.info("scheduling initiated for group ID {}", groupId);
        long start = System.currentTimeMillis();
        String msg = String.valueOf(groupId);
        eventService.create(creationTime, start, Event.Action.INITIATED, Event.Entity.SCHEDULING, msg, ops);
        Schedule schedule = optimisationService.generateOptimalSchedule(groupId, ops);
        long end = System.currentTimeMillis();
        log.info("scheduling completed for group ID {}", groupId);
        eventService.create(start, end, Event.Action.COMPLETED, Event.Entity.SCHEDULING, msg, schedule);
        scheduleService.save(schedule);
    }
}