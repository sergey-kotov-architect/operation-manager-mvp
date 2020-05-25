package com.sergeykotov.operationmanagermvp.controller;

import com.sergeykotov.operationmanagermvp.model.Executor;
import com.sergeykotov.operationmanagermvp.service.ExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/executor")
public class ExecutorController {
    private final ExecutorService executorService;

    @Autowired
    public ExecutorController(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @GetMapping
    public List<Executor> extract() {
        return executorService.extract();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody @Valid Executor executor) {
        executorService.create(executor);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody @Valid Executor executor) {
        executorService.update(executor);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestBody @Valid Executor executor) {
        executorService.delete(executor);
    }
}