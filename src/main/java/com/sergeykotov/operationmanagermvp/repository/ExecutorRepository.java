package com.sergeykotov.operationmanagermvp.repository;

import com.sergeykotov.operationmanagermvp.model.Executor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ExecutorRepository {
    private static final String EXTRACT_CMD = "select e.id, e.name, e.note from executor e";
    private static final String CREATE_CMD = "insert into executor (name, note) values (?, ?)";
    private static final String UPDATE_CMD = "update executor set name = ?, note = ? where id = ?";
    private static final String DELETE_CMD = "delete from executor where id = ?";

    public List<Executor> extract() throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXTRACT_CMD);
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

    public boolean create(Executor executor) throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE_CMD)) {
            preparedStatement.setString(1, executor.getName());
            preparedStatement.setString(2, executor.getNote());
            return preparedStatement.executeUpdate() == 1;
        }
    }

    public boolean update(Executor executor) throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CMD)) {
            preparedStatement.setString(1, executor.getName());
            preparedStatement.setString(2, executor.getNote());
            preparedStatement.setLong(3, executor.getId());
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
}