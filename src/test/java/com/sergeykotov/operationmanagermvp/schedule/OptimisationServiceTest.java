package com.sergeykotov.operationmanagermvp.schedule;

import com.sergeykotov.operationmanagermvp.model.Executor;
import com.sergeykotov.operationmanagermvp.model.Op;
import com.sergeykotov.operationmanagermvp.model.Period;
import com.sergeykotov.operationmanagermvp.model.Task;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OptimisationServiceTest {
    private OptimisationService optimisationService;

    @Before
    public void setUp() {
        optimisationService = new OptimisationService();
    }

    @Test
    public void generateOptimalSchedule() {
        Executor executor1 = new Executor(1);
        Executor executor2 = new Executor(2);
        Executor executor3 = new Executor(3);

        Task task1 = new Task(1);
        Task task2 = new Task(2);
        Task task3 = new Task(3);

        Period period = new Period(1);

        Op op11 = new Op(1, Op.Status.UNSCHEDULED, 1.0, task1, executor1, period);
        Op op21 = new Op(2, Op.Status.UNSCHEDULED, 1.0, task1, executor2, period);
        Op op22 = new Op(3, Op.Status.UNSCHEDULED, 1.0, task2, executor2, period);
        Op op32 = new Op(4, Op.Status.UNSCHEDULED, 1.0, task2, executor3, period);
        Op op33 = new Op(5, Op.Status.UNSCHEDULED, 1.0, task3, executor3, period);

        List<Op> ops = new ArrayList<>();
        ops.add(op11);
        ops.add(op21);
        ops.add(op22);
        ops.add(op32);
        ops.add(op33);

        List<Op> expectedSchedule = new ArrayList<>();
        expectedSchedule.add(op11);
        expectedSchedule.add(op22);
        expectedSchedule.add(op33);

        assertEquals(expectedSchedule, optimisationService.generateOptimalSchedule(ops));
    }
}