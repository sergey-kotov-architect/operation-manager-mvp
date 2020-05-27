package com.sergeykotov.operationmanagermvp.repository;

import com.sergeykotov.operationmanagermvp.model.Period;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PeriodRepository {
    private static final String EXTRACT_ALL_CMD = "select p.id, p.name, p.note, p.start_time, p.end_time from period p";
    private static final String EXTRACT_BY_ID_CMD = "select p.name, p.note, p.start_time, p.end_time from period p where p.id = ?";
    private static final String CREATE_CMD = "insert into period (name, note, start_time, end_time) values (?,?,?,?)";
    private static final String UPDATE_CMD = "update period set name = ?, note = ?, start_time = ?, end_time = ? where id = ?";
    private static final String DELETE_CMD = "delete from period where id = ?";

    public List<Period> extractAll() throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXTRACT_ALL_CMD);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            List<Period> periods = new ArrayList<>();
            while (resultSet.next()) {
                Period period = new Period();
                period.setId(resultSet.getLong("id"));
                period.setName(resultSet.getString("name"));
                period.setNote(resultSet.getString("note"));
                period.setStart(resultSet.getLong("start_time"));
                period.setEnd(resultSet.getLong("end_time"));
                periods.add(period);
            }
            return periods;
        }
    }

    public Optional<Period> extractById(long id) throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXTRACT_BY_ID_CMD)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Period period = new Period();
                    period.setId(id);
                    period.setName(resultSet.getString("name"));
                    period.setNote(resultSet.getString("note"));
                    period.setStart(resultSet.getLong("start_time"));
                    period.setEnd(resultSet.getLong("end_time"));
                    return Optional.of(period);
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    public void create(Period period) throws SQLException {
        boolean succeeded;
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE_CMD)) {
            preparedStatement.setString(1, period.getName());
            preparedStatement.setString(2, period.getNote());
            preparedStatement.setLong(3, period.getStart());
            preparedStatement.setLong(4, period.getEnd());
            succeeded = preparedStatement.executeUpdate() == 1;
        }
        if (!succeeded) {
            throw new SQLException();
        }
    }

    public void updateById(Period period) throws SQLException {
        boolean succeeded;
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CMD)) {
            preparedStatement.setString(1, period.getName());
            preparedStatement.setString(2, period.getNote());
            preparedStatement.setLong(3, period.getStart());
            preparedStatement.setLong(4, period.getEnd());
            preparedStatement.setLong(5, period.getId());
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