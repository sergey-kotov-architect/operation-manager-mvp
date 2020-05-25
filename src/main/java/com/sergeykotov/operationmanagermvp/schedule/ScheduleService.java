package com.sergeykotov.operationmanagermvp.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {
    private static final Logger log = LoggerFactory.getLogger(ScheduleService.class);

    public Schedule extract() {
        throw new UnsupportedOperationException(); //TODO: implement ScheduleService::extract
    }

    public Schedule extract(long groupId) {
        throw new UnsupportedOperationException(); //TODO: implement ScheduleService::extract by ID
    }

    public void createSchedulingTask(long groupId) {
        throw new UnsupportedOperationException(); //TODO: implement ScheduleService::createSchedulingTask
    }
}