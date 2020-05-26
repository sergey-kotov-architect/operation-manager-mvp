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
    private static final String EXTRACT_CMD = "select g.id, g.name, g.note from op_group g";
    private static final String FIND_BY_ID_CMD = "select g.name, g.note, g.metrics from op_group g where g.id = ?";
    private static final String CREATE_CMD = "insert into op_group (name, note) values (?, ?)";
    private static final String UPDATE_CMD = "update op_group set name = ?, note = ?, metrics = ? where id = ?";
    private static final String UPDATE_METRICS_CMD = "update op_group set metrics = ? where id = ?";
    private static final String DELETE_CMD = "delete from op_group where id = ?";

    public List<Group> extract() throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXTRACT_CMD);
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

    public Optional<Group> findById(long id) throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_CMD)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Group group = new Group();
                    group.setId(resultSet.getLong("id"));
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

    public boolean create(Group group) throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE_CMD)) {
            preparedStatement.setString(1, group.getName());
            preparedStatement.setString(2, group.getNote());
            return preparedStatement.executeUpdate() == 1;
        }
    }

    public boolean update(Group group) throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CMD)) {
            preparedStatement.setString(1, group.getName());
            preparedStatement.setString(2, group.getNote());
            preparedStatement.setString(3, group.getMetrics());
            preparedStatement.setLong(4, group.getId());
            return preparedStatement.executeUpdate() == 1;
        }
    }

    public boolean updateMetricsById(long id, String metrics) throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_METRICS_CMD)) {
            preparedStatement.setString(1, metrics);
            preparedStatement.setLong(2, id);
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