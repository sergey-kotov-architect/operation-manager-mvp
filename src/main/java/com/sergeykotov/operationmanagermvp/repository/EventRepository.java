package com.sergeykotov.operationmanagermvp.repository;

import com.sergeykotov.operationmanagermvp.event.Event;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EventRepository {
    private static final String EXTRACT_CMD = "select e.id, e.start_timestamp, e.start_time, e.end_timestamp, e.end_time, e.elapsed, e.action, e.entity, e.name, e.user, e.note from event e";
    private static final String EXTRACT_SINCE_CMD = "select e.id, e.start_timestamp, e.start_time, e.end_timestamp, e.end_time, e.elapsed, e.action, e.entity, e.name, e.user, e.note from event e where e.start_time >= ?";
    private static final String CREATE_CMD = "insert into event (start_timestamp,start_time,end_timestamp,end_time,elapsed,action,entity,name,user,note) values (?,?,?,?,?,?,?,?)";

    private List<Event> extract(ResultSet resultSet) throws SQLException {
        List<Event> events = new ArrayList<>();
        while (resultSet.next()) {
            Event event = new Event();
            event.setId(resultSet.getLong("id"));
            event.setStartTimestamp(resultSet.getLong("start_timestamp"));
            event.setStart(resultSet.getTimestamp("start_time").toLocalDateTime());
            event.setEndTimestamp(resultSet.getLong("end_timestamp"));
            event.setEnd(resultSet.getTimestamp("end_time").toLocalDateTime());
            event.setElapsed(resultSet.getLong("elapsed"));
            event.setAction(Event.Action.valueOf(resultSet.getString("action")));
            event.setEntity(Event.Entity.valueOf(resultSet.getString("entity")));
            event.setName(resultSet.getString("name"));
            event.setUser(resultSet.getString("user"));
            event.setNote(resultSet.getString("note"));
            events.add(event);
        }
        return events;
    }

    public List<Event> extract() throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXTRACT_CMD);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            return extract(resultSet);
        }
    }

    public List<Event> extract(long since) throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXTRACT_SINCE_CMD)) {
            preparedStatement.setLong(1, since);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return extract(resultSet);
            }
        }
    }

    public boolean create(Event event) throws SQLException {
        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE_CMD)) {
            preparedStatement.setLong(1, event.getStartTimestamp());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(event.getStart()));
            preparedStatement.setLong(3, event.getEndTimestamp());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(event.getEnd()));
            preparedStatement.setLong(5, event.getElapsed());
            preparedStatement.setString(6, event.getAction().name());
            preparedStatement.setString(7, event.getEntity().name());
            preparedStatement.setString(8, event.getName());
            preparedStatement.setString(9, event.getUser());
            preparedStatement.setString(10, event.getNote());
            return preparedStatement.executeUpdate() == 1;
        }
    }
}