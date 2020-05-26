package com.sergeykotov.operationmanagermvp.service;

import com.sergeykotov.operationmanagermvp.event.Event;
import com.sergeykotov.operationmanagermvp.event.EventService;
import com.sergeykotov.operationmanagermvp.exception.DatabaseException;
import com.sergeykotov.operationmanagermvp.exception.ExtractionException;
import com.sergeykotov.operationmanagermvp.model.Task;
import com.sergeykotov.operationmanagermvp.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final EventService eventService;
    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(EventService eventService, TaskRepository taskRepository) {
        this.eventService = eventService;
        this.taskRepository = taskRepository;
    }

    public List<Task> extract() {
        try {
            return taskRepository.extract();
        } catch (Exception e) {
            log.error("failed to extract tasks", e);
            throw new ExtractionException();
        }
    }

    public void create(Task task) {
        log.info("creating task {}", task);
        long start = System.currentTimeMillis();
        try {
            taskRepository.create(task);
        } catch (Exception e) {
            log.error("failed to create task {}", task, e);
            throw new DatabaseException();
        }
        long end = System.currentTimeMillis();
        log.info("task {} created", task);
        eventService.create(start, end, Event.Action.CREATED, Event.Entity.TASK, task.toString(), task);
    }

    public void update(long id, Task task) {
        task.setId(id);
        log.info("updating task {}", task);
        long start = System.currentTimeMillis();
        try {
            taskRepository.update(task);
        } catch (Exception e) {
            log.error("failed to update task {}", task, e);
            throw new DatabaseException();
        }
        long end = System.currentTimeMillis();
        log.info("task {} updated", task);
        eventService.create(start, end, Event.Action.UPDATED, Event.Entity.TASK, task.toString(), task);
    }

    public void delete(long id, Task task) {
        task.setId(id);
        log.info("deleting task {}", task);
        long start = System.currentTimeMillis();
        try {
            taskRepository.delete(task.getId());
        } catch (Exception e) {
            log.error("failed to delete task {}", task, e);
            throw new DatabaseException();
        }
        long end = System.currentTimeMillis();
        log.info("task {} deleted", task);
        eventService.create(start, end, Event.Action.DELETED, Event.Entity.TASK, task.toString(), task);
    }
}