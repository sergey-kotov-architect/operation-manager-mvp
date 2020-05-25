package com.sergeykotov.operationmanagermvp.schedule;

import com.sergeykotov.operationmanagermvp.model.Executor;
import com.sergeykotov.operationmanagermvp.model.Op;
import com.sergeykotov.operationmanagermvp.model.Period;
import com.sergeykotov.operationmanagermvp.model.Task;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OptimisationService {
    public Schedule generateOptimalSchedule(long groupId, List<Op> ops) {
        List<List<Op>> schedules = getAllPossibleSchedules(ops);
        List<Op> schedule = selectOptimalSchedule(schedules);
        schedule.stream()
                .filter(o -> o.getStatus().equals(Op.Status.UNSCHEDULED) || o.getStatus().equals(Op.Status.CANCELLED))
                .forEach(o -> o.setStatus(Op.Status.SCHEDULED));
        return new Schedule(groupId, schedule);
    }

    private List<List<Op>> getAllPossibleSchedules(List<Op> ops) {
        List<List<Op>> schedules = new ArrayList<>();
        Set<Task> tasks = ops.stream().map(Op::getTask).collect(Collectors.toSet());
        Set<Period> periods = ops.stream().map(Op::getPeriod).collect(Collectors.toSet());
        for (Task task : tasks) {
            for (Period period : periods) {
                List<Op> opsForExecutor = ops.stream()
                        .filter(o -> o.getTask().equals(task) && o.getPeriod().equals(period))
                        .collect(Collectors.toList());
                List<List<Op>> newSchedules = new ArrayList<>();
                for (Op op : opsForExecutor) {
                    if (schedules.isEmpty()) {
                        List<Op> newSchedule = new ArrayList<>();
                        newSchedule.add(op);
                        newSchedules.add(newSchedule);
                    } else {
                        for (List<Op> schedule : schedules) {
                            List<Op> newSchedule = new ArrayList<>(schedule);
                            newSchedule.add(op);
                            newSchedules.add(newSchedule);
                        }
                    }
                }
                schedules = newSchedules;
            }
        }
        return schedules;
    }

    private List<Op> selectOptimalSchedule(List<List<Op>> schedules) {
        return schedules.stream().min(Comparator.comparingDouble(this::deviation)).orElse(Collections.emptyList());
    }

    private double deviation(List<Op> ops) {
        Set<Executor> executors = ops.stream().map(Op::getExecutor).collect(Collectors.toSet());
        double mean = ops.stream().mapToDouble(Op::getCost).sum() / executors.size();
        double deviation = 0.0;
        for (Executor executor : executors) {
            double cost = ops.stream().filter(o -> o.getExecutor().equals(executor)).mapToDouble(Op::getCost).sum();
            deviation += Math.abs(cost - mean);
        }
        return deviation;
    }
}