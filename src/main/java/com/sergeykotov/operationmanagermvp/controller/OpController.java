package com.sergeykotov.operationmanagermvp.controller;

import com.sergeykotov.operationmanagermvp.model.Op;
import com.sergeykotov.operationmanagermvp.service.OpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/op")
public class OpController {
    private final OpService opService;

    @Autowired
    public OpController(OpService opService) {
        this.opService = opService;
    }

    @GetMapping
    public List<Op> extract() {
        return opService.extract();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody @Valid Op op) {
        opService.create(op);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody @Valid Op op) {
        opService.update(op);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestBody @Valid Op op) {
        opService.delete(op);
    }
}