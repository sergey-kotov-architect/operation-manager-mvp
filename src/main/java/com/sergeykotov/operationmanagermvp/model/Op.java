package com.sergeykotov.operationmanagermvp.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Objects;

public class Op {
    private long id;

    @NotEmpty
    private String name;

    private String note;

    @NotNull
    private Status status;

    @Positive
    private double profit;

    @Positive
    private double cost;

    @NotNull
    private Group group;

    @NotNull
    private Task task;

    @NotNull
    private Executor executor;

    @NotNull
    private Period period;

    public enum Status {UNSCHEDULED, SCHEDULED, CANCELLED, EXECUTING, COMPLETED, FAILED}

    public Op() {
    }

    public Op(long id,
              @NotNull Status status,
              @Positive double cost,
              @NotNull Task task,
              @NotNull Executor executor,
              @NotNull Period period) {
        this.id = id;
        this.status = status;
        this.cost = cost;
        this.task = task;
        this.executor = executor;
        this.period = period;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Op op = (Op) o;
        return getId() == op.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return getName();
    }
}