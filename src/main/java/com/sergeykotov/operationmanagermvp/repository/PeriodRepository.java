package com.sergeykotov.operationmanagermvp.repository;

import com.sergeykotov.operationmanagermvp.model.Period;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PeriodRepository {
    private static final String EXTRACT_CMD = "select p.id, p.name, p.note, p.start_time, p.end_time from period p";
    private static final String CREATE_CMD = "insert into period (name, note, start_time, end_time) values (?,?,?,?)";
    private static final String UPDATE_CMD = "update period set name = ?, note = ?, start_time = ?, end_time = ? where id = ?";
    private static final String DELETE_CMD = "delete from period where id = ?";

    public List<Period> extract() throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXTRACT_CMD);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            List<Period> periods = new ArrayList<>();
            while (resultSet.next()) {
                Period period = new Period();
                period.setId(resultSet.getLong("id"));
                period.setName(resultSet.getString("name"));
                period.setNote(resultSet.getString("note"));
                period.setStart(resultSet.getTimestamp("start_time").toLocalDateTime());
                period.setEnd(resultSet.getTimestamp("end_time").toLocalDateTime());
                periods.add(period);
            }
            return periods;
        }
    }

    public boolean create(Period period) throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE_CMD)) {
            preparedStatement.setString(1, period.getName());
            preparedStatement.setString(2, period.getNote());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(period.getStart()));
            preparedStatement.setTimestamp(4, Timestamp.valueOf(period.getEnd()));
            return preparedStatement.executeUpdate() == 1;
        }
    }

    public boolean update(Period period) throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CMD)) {
            preparedStatement.setString(1, period.getName());
            preparedStatement.setString(2, period.getNote());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(period.getStart()));
            preparedStatement.setTimestamp(4, Timestamp.valueOf(period.getEnd()));
            preparedStatement.setLong(5, period.getId());
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