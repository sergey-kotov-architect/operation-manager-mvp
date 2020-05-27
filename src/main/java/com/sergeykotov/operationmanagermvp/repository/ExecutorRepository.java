package com.sergeykotov.operationmanagermvp.repository;

import com.sergeykotov.operationmanagermvp.model.Executor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ExecutorRepository {
    private static final String EXTRACT_ALL_CMD = "select e.id, e.name, e.note from executor e";
    private static final String EXTRACT_BY_ID_CMD = "select e.name, e.note from executor e where e.id = ?";
    private static final String CREATE_CMD = "insert into executor (name, note) values (?, ?)";
    private static final String UPDATE_CMD = "update executor set name = ?, note = ? where id = ?";
    private static final String DELETE_CMD = "delete from executor where id = ?";

    public List<Executor> extractAll() throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXTRACT_ALL_CMD);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            List<Executor> executors = new ArrayList<>();
            while (resultSet.next()) {
                Executor executor = new Executor();
                executor.setId(resultSet.getLong("id"));
                executor.setName(resultSet.getString("name"));
                executor.setNote(resultSet.getString("note"));
                executors.add(executor);
            }
            return executors;
        }
    }

    public Optional<Executor> extractById(long id) throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXTRACT_BY_ID_CMD)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Executor executor = new Executor();
                    executor.setId(id);
                    executor.setName(resultSet.getString("name"));
                    executor.setNote(resultSet.getString("note"));
                    return Optional.of(executor);
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    public void create(Executor executor) throws SQLException {
        boolean succeeded;
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE_CMD)) {
            preparedStatement.setString(1, executor.getName());
            preparedStatement.setString(2, executor.getNote());
            succeeded = preparedStatement.executeUpdate() == 1;
        }
        if (!succeeded) {
            throw new SQLException();
        }
    }

    public void updateById(Executor executor) throws SQLException {
        boolean succeeded;
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CMD)) {
            preparedStatement.setString(1, executor.getName());
            preparedStatement.setString(2, executor.getNote());
            preparedStatement.setLong(3, executor.getId());
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