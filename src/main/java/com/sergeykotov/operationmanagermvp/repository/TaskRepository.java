package com.sergeykotov.operationmanagermvp.repository;

import com.sergeykotov.operationmanagermvp.model.Task;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TaskRepository {
    private static final String EXTRACT_ALL_CMD = "select t.id, t.name, t.note from task t";
    private static final String EXTRACT_BY_ID_CMD = "select t.name, t.note from task t where t.id = ?";
    private static final String CREATE_CMD = "insert into task (name, note) values (?, ?)";
    private static final String UPDATE_CMD = "update task set name = ?, note = ? where id = ?";
    private static final String DELETE_CMD = "delete from task where id = ?";

    public List<Task> extractAll() throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXTRACT_ALL_CMD);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            List<Task> tasks = new ArrayList<>();
            while (resultSet.next()) {
                Task task = new Task();
                task.setId(resultSet.getLong("id"));
                task.setName(resultSet.getString("name"));
                task.setNote(resultSet.getString("note"));
                tasks.add(task);
            }
            return tasks;
        }
    }

    public Optional<Task> extractById(long id) throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXTRACT_BY_ID_CMD)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Task task = new Task();
                    task.setId(id);
                    task.setName(resultSet.getString("name"));
                    task.setNote(resultSet.getString("note"));
                    return Optional.of(task);
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    public void create(Task task) throws SQLException {
        boolean succeeded;
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE_CMD)) {
            preparedStatement.setString(1, task.getName());
            preparedStatement.setString(2, task.getNote());
            succeeded = preparedStatement.executeUpdate() == 1;
        }
        if (!succeeded) {
            throw new SQLException();
        }
    }

    public void updateById(Task task) throws SQLException {
        boolean succeeded;
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CMD)) {
            preparedStatement.setString(1, task.getName());
            preparedStatement.setString(2, task.getNote());
            preparedStatement.setLong(3, task.getId());
            succeeded = preparedStatement.executeUpdate() == 1;
        }
        if (!succeeded) {
            throw new SQLException();
        }
    }

    public void deleteById(long id) throws SQLException {
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
}