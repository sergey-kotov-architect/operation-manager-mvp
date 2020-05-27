package com.sergeykotov.operationmanagermvp.schedule;

import com.sergeykotov.operationmanagermvp.model.Op;

import java.util.List;

public class Schedule {
    private Long groupId;
    private List<Op> ops;

    public Schedule(List<Op> ops) {
        this.ops = ops;
    }

    public Schedule(Long groupId, List<Op> ops) {
        this.groupId = groupId;
        this.ops = ops;
    }

    public Long getGroupId() {
        return groupId;
    }

    public List<Op> getOps() {
        return ops;
    }

    @Override
    public String toString() {
        return String.valueOf(groupId);
    }
}