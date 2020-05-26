package com.sergeykotov.operationmanagermvp.event;

import java.time.LocalDateTime;
import java.util.Objects;

public class Event {
    private long id;
    private long startTimestamp;
    private LocalDateTime start;
    private long endTimestamp;
    private LocalDateTime end;
    private long elapsed;
    private Action action;
    private Entity entity;
    private String name;
    private String user;
    private String note;

    public enum Action {
        CREATED, UPDATED, DELETED, INITIATED, COMPLETED, SAVED, FAILED_TO_SAVE
    }

    public enum Entity {
        TASK, EXECUTOR, PERIOD, GROUP, OP, SCHEDULING, SCHEDULE
    }

    public Event() {
    }

    public Event(long startTimestamp,
                 LocalDateTime start,
                 long endTimestamp,
                 LocalDateTime end,
                 long elapsed,
                 Action action,
                 Entity entity,
                 String name,
                 String user,
                 String note) {
        this.startTimestamp = startTimestamp;
        this.start = start;
        this.endTimestamp = endTimestamp;
        this.end = end;
        this.elapsed = elapsed;
        this.action = action;
        this.entity = entity;
        this.name = name;
        this.user = user;
        this.note = note;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public long getElapsed() {
        return elapsed;
    }

    public void setElapsed(long elapsed) {
        this.elapsed = elapsed;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return getId() == event.getId();
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