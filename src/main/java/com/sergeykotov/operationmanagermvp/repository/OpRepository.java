package com.sergeykotov.operationmanagermvp.repository;

import com.sergeykotov.operationmanagermvp.model.Op;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OpRepository {
    private static final String EXTRACT_CMD = "select o.id, o.name, o.note, o.status, o.profit, o.cost, o.group_id, o.task_id, o.executor_id, o.period_id from op o";
    private static final String CREATE_CMD = "insert into op (name, note, status, profit, cost, group_id, task_id, executor_id, period_id) values (?,?,?,?,?,?,?,?,?)";
    private static final String UPDATE_CMD = "update op set name = ?, note = ?, status = ?, profit = ?, cost = ?, group_id = ?, task_id = ?, executor_id = ?, period_id = ? where id = ?";
    private static final String DELETE_CMD = "delete from op where id = ?";
    private static final String UPDATE_STATUS_CMD = "update op set status = ? where id = ?";

    public List<Op> extract() throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXTRACT_CMD);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            List<Op> ops = new ArrayList<>();
            while (resultSet.next()) {
                Op op = new Op();
                op.setId(resultSet.getLong("id"));
                op.setName(resultSet.getString("name"));
                op.setNote(resultSet.getString("note"));
                op.setStatus(Op.Status.valueOf(resultSet.getString("status")));
                op.setProfit(resultSet.getDouble("profit"));
                op.setCost(resultSet.getDouble("cost"));
                ops.add(op);
            }
            return ops;
        }
    }

    public boolean create(Op op) throws SQLException {
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
            return preparedStatement.executeUpdate() == 1;
        }
    }

    public boolean update(Op op) throws SQLException {
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
            return preparedStatement.executeUpdate() == 1;
        }
    }

    public boolean delete(long id) throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_CMD)) {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() == 1;
        }
    }

    public int[] updateStatus(List<Op> ops) throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_STATUS_CMD)) {
            for (Op op : ops) {
                preparedStatement.setString(1, op.getStatus().name());
                preparedStatement.setLong(2, op.getId());
                preparedStatement.addBatch();
            }
            return preparedStatement.executeBatch();
        }
    }
}