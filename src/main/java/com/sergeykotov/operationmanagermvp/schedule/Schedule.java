package com.sergeykotov.operationmanagermvp.schedule;

import com.sergeykotov.operationmanagermvp.model.Op;

import java.util.List;

public class Schedule {
    private long groupId;
    private List<Op> ops;

    public Schedule(long groupId, List<Op> ops) {
        this.groupId = groupId;
        this.ops = ops;
    }

    public long getGroupId() {
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