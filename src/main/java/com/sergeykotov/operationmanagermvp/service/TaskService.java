package com.sergeykotov.operationmanagermvp.service;

import com.sergeykotov.operationmanagermvp.event.Event;
import com.sergeykotov.operationmanagermvp.event.EventService;
import com.sergeykotov.operationmanagermvp.exception.DatabaseException;
import com.sergeykotov.operationmanagermvp.exception.ExtractionException;
import com.sergeykotov.operationmanagermvp.exception.NotFoundException;
import com.sergeykotov.operationmanagermvp.model.Task;
import com.sergeykotov.operationmanagermvp.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public List<Task> extractAll() {
        try {
            return taskRepository.extractAll();
        } catch (Exception e) {
            log.error("failed to extract tasks", e);
            throw new ExtractionException();
        }
    }

    public Task extractById(long id) {
        Optional<Task> task;
        try {
            task = taskRepository.extractById(id);
        } catch (Exception e) {
            log.error("failed to extract task by ID {}", id, e);
            throw new ExtractionException();
        }
        return task.orElseThrow(NotFoundException::new);
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

    public void updateById(long id, Task task) {
        task.setId(id);
        Task currentTask = extractById(id);
        log.info("updating task {} to {}", currentTask, task);
        long start = System.currentTimeMillis();
        try {
            taskRepository.updateById(task);
        } catch (Exception e) {
            log.error("failed to update task {} to {}", currentTask, task, e);
            throw new DatabaseException();
        }
        long end = System.currentTimeMillis();
        log.info("task {} updated to {}", currentTask, task);
        eventService.create(start, end, Event.Action.UPDATED, Event.Entity.TASK, task.toString(), task);
    }

    public void deleteById(long id) {
        Task task = extractById(id);
        log.info("deleting task {}", task);
        long start = System.currentTimeMillis();
        try {
            taskRepository.deleteById(id);
        } catch (Exception e) {
            log.error("failed to delete task {}", task, e);
            throw new DatabaseException();
        }
        long end = System.currentTimeMillis();
        log.info("task {} deleted", task);
        eventService.create(start, end, Event.Action.DELETED, Event.Entity.TASK, task.toString(), task);
    }
}