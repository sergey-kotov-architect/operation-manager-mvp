package com.sergeykotov.operationmanagermvp.controller;

import com.sergeykotov.operationmanagermvp.schedule.Schedule;
import com.sergeykotov.operationmanagermvp.schedule.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public Schedule extract() {
        return scheduleService.extract();
    }

    @GetMapping("/{id}")
    public Schedule extractByGroupId(@PathVariable long id) {
        return scheduleService.extractByGroupId(id);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void createSchedulingTask(@PathVariable long id) {
        scheduleService.createSchedulingTask(id);
    }
}