package com.sergeykotov.operationmanagermvp.repository;

import com.sergeykotov.operationmanagermvp.model.*;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class OpRepository {
    private static final String EXTRACT_CMD =
            "select o.id, o.name, o.note, o.status, o.profit, o.cost, o.group_id, o.task_id, o.executor_id, o.period_id, " +
                    " g.name as g_name, t.name as t_name, e.name as e_name, p.name as p_name, p.start_time as start, p.end_time as end " +
                    "from op o join op_group g on g.id = o.group_id join task t on o.task_id = t.id " +
                    "join executor e on o.executor_id = e.id join period p on o.period_id = p.id";
    private static final String EXTRACT_BY_GROUP_ID_CMD =
            "select o.id, o.name, o.note, o.status, o.profit, o.cost, o.group_id, o.task_id, o.executor_id, o.period_id, " +
                    " g.name as g_name, t.name as t_name, e.name as e_name, p.name as p_name, p.start_time as start, p.end_time as end " +
                    "from op o join op_group g on g.id = o.group_id join task t on o.task_id = t.id " +
                    "join executor e on o.executor_id = e.id join period p on o.period_id = p.id where o.group_id = ?";
    private static final String EXTRACT_SCHEDULE_CMD =
            "select o.id, o.name, o.note, o.status, o.profit, o.cost, o.group_id, o.task_id, o.executor_id, o.period_id, " +
                    " g.name as g_name, t.name as t_name, e.name as e_name, p.name as p_name, p.start_time as start, p.end_time as end " +
                    "from op o join op_group g on g.id = o.group_id join task t on o.task_id = t.id " +
                    "join executor e on o.executor_id = e.id join period p on o.period_id = p.id where o.status <> ?";
    private static final String EXTRACT_SCHEDULE_BY_ID_CMD =
            "select o.id, o.name, o.note, o.status, o.profit, o.cost, o.group_id, o.task_id, o.executor_id, o.period_id, " +
                    " g.name as g_name, t.name as t_name, e.name as e_name, p.name as p_name, p.start_time as start, p.end_time as end " +
                    "from op o join op_group g on g.id = o.group_id join task t on o.task_id = t.id " +
                    "join executor e on o.executor_id = e.id join period p on o.period_id = p.id " +
                    "where o.status <> ? and o.group_id = ?";
    private static final String CREATE_CMD = "insert into op (name, note, status, profit, cost, group_id, task_id, executor_id, period_id) values (?,?,?,?,?,?,?,?,?)";
    private static final String UPDATE_CMD = "update op set name = ?, note = ?, status = ?, profit = ?, cost = ?, group_id = ?, task_id = ?, executor_id = ?, period_id = ? where id = ?";
    private static final String DELETE_CMD = "delete from op where id = ?";
    private static final String UPDATE_STATUS_CMD = "update op set status = ? where id = ?";

    private List<Op> extract(ResultSet resultSet) throws SQLException {
        List<Op> ops = new ArrayList<>();
        while (resultSet.next()) {
            Op op = new Op();
            op.setId(resultSet.getLong("id"));
            op.setName(resultSet.getString("name"));
            op.setNote(resultSet.getString("note"));
            op.setStatus(Op.Status.valueOf(resultSet.getString("status")));
            op.setProfit(resultSet.getDouble("profit"));
            op.setCost(resultSet.getDouble("cost"));

            Group group = new Group();
            group.setId(resultSet.getLong("group_id"));
            group.setName(resultSet.getString("g_name"));
            op.setGroup(group);

            Task task = new Task();
            task.setId(resultSet.getLong("task_id"));
            task.setName(resultSet.getString("t_name"));
            op.setTask(task);

            Executor executor = new Executor();
            executor.setId(resultSet.getLong("executor_id"));
            executor.setName(resultSet.getString("e_name"));
            op.setExecutor(executor);

            Period period = new Period();
            period.setId(resultSet.getLong("period_id"));
            period.setName(resultSet.getString("p_name"));
            long start = resultSet.getLong("start");
            period.setStart(ZonedDateTime.ofInstant(Instant.ofEpochMilli(start), ZoneId.systemDefault()));
            long end = resultSet.getLong("end");
            period.setEnd(ZonedDateTime.ofInstant(Instant.ofEpochMilli(end), ZoneId.systemDefault()));
            op.setPeriod(period);

            ops.add(op);
        }
        return ops;
    }

    public List<Op> extract() throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXTRACT_CMD);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            return extract(resultSet);
        }
    }

    public List<Op> extract(long groupId) throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXTRACT_BY_GROUP_ID_CMD)) {
            preparedStatement.setLong(1, groupId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return extract(resultSet);
            }
        }
    }

    public List<Op> extractSchedule() throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXTRACT_SCHEDULE_CMD)) {
            preparedStatement.setString(1, Op.Status.UNSCHEDULED.name());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return extract(resultSet);
            }
        }
    }

    public List<Op> extractSchedule(long groupId) throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXTRACT_SCHEDULE_BY_ID_CMD)) {
            preparedStatement.setString(1, Op.Status.UNSCHEDULED.name());
            preparedStatement.setLong(2, groupId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return extract(resultSet);
            }
        }
    }

    public void create(Op op) throws SQLException {
        boolean succeeded;
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE_CMD)) {
            preparedStatement.setString(1, op.getName());
            preparedStatement.setString(2, op.getNote());
            preparedStatement.setString(3, op.getStatus().name());
            preparedStatement.setDouble(4, op.getProfit());
            preparedStatement.setDouble(5, op.getCost());
            preparedStatement.setLong(6, op.getGroup().getId());
            preparedStatement.setLong(7, op.getTask().getId());
            preparedStatement.setLong(8, op.getExecutor().getId());
            preparedStatement.setLong(9, op.getPeriod().getId());
            succeeded = preparedStatement.executeUpdate() == 1;
        }
        if (!succeeded) {
            throw new SQLException();
        }
    }

    public void update(Op op) throws SQLException {
        boolean succeeded;
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CMD)) {
            preparedStatement.setString(1, op.getName());
            preparedStatement.setString(2, op.getNote());
            preparedStatement.setString(3, op.getStatus().name());
            preparedStatement.setDouble(4, op.getProfit());
            preparedStatement.setDouble(5, op.getCost());
            preparedStatement.setLong(6, op.getGroup().getId());
            preparedStatement.setLong(7, op.getTask().getId());
            preparedStatement.setLong(8, op.getExecutor().getId());
            preparedStatement.setLong(9, op.getPeriod().getId());
            preparedStatement.setLong(10, op.getId());
            succeeded = preparedStatement.executeUpdate() == 1;
        }
        if (!succeeded) {
            throw new SQLException();
        }
    }

    public void delete(long id) throws SQLException {
        boolean succeeded;
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_CMD)) {
            preparedStatement.setLong(1, id);
            succeeded = preparedStatement.executeUpdate() == 1;
        }
        if (!succeeded) {
            throw new SQLException();
        }
    }

    public void updateStatus(List<Op> ops) throws SQLException {
        boolean succeeded;
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_STATUS_CMD)) {
            for (Op op : ops) {
                preparedStatement.setString(1, op.getStatus().name());
                preparedStatement.setLong(2, op.getId());
                preparedStatement.addBatch();
            }
            succeeded = Arrays.stream(preparedStatement.executeBatch()).count() == ops.size();
        }
        if (!succeeded) {
            throw new SQLException();
        }
    }
}