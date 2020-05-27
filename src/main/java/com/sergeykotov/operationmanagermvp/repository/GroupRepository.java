package com.sergeykotov.operationmanagermvp.repository;

import com.sergeykotov.operationmanagermvp.model.Group;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class GroupRepository {
    private static final String EXTRACT_ALL_CMD = "select g.id, g.name, g.note from op_group g";
    private static final String EXTRACT_BY_ID_CMD = "select g.name, g.note, g.metrics from op_group g where g.id = ?";
    private static final String CREATE_CMD = "insert into op_group (name, note) values (?, ?)";
    private static final String UPDATE_CMD = "update op_group set name = ?, note = ?, metrics = ? where id = ?";
    private static final String UPDATE_METRICS_CMD = "update op_group set metrics = ? where id = ?";
    private static final String DELETE_CMD = "delete from op_group where id = ?";

    public List<Group> extractAll() throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXTRACT_ALL_CMD);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            List<Group> groups = new ArrayList<>();
            while (resultSet.next()) {
                Group group = new Group();
                group.setId(resultSet.getLong("id"));
                group.setName(resultSet.getString("name"));
                group.setNote(resultSet.getString("note"));
                groups.add(group);
            }
            return groups;
        }
    }

    public Optional<Group> extractById(long id) throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXTRACT_BY_ID_CMD)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Group group = new Group();
                    group.setId(id);
                    group.setName(resultSet.getString("name"));
                    group.setNote(resultSet.getString("note"));
                    group.setMetrics(resultSet.getString("metrics"));
                    return Optional.of(group);
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    public void create(Group group) throws SQLException {
        boolean succeeded;
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE_CMD)) {
            preparedStatement.setString(1, group.getName());
            preparedStatement.setString(2, group.getNote());
            succeeded = preparedStatement.executeUpdate() == 1;
        }
        if (!succeeded) {
            throw new SQLException();
        }
    }

    public void updateById(Group group) throws SQLException {
        boolean succeeded;
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CMD)) {
            preparedStatement.setString(1, group.getName());
            preparedStatement.setString(2, group.getNote());
            preparedStatement.setString(3, group.getMetrics());
            preparedStatement.setLong(4, group.getId());
            succeeded = preparedStatement.executeUpdate() == 1;
        }
        if (!succeeded) {
            throw new SQLException();
        }
    }

    public void updateMetricsById(long id, String metrics) throws SQLException {
        boolean succeeded;
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_METRICS_CMD)) {
            preparedStatement.setString(1, metrics);
            preparedStatement.setLong(2, id);
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