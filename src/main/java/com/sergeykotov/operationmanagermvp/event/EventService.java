package com.sergeykotov.operationmanagermvp.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergeykotov.operationmanagermvp.exception.ExtractionException;
import com.sergeykotov.operationmanagermvp.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EventService {
    private static final Logger log = LoggerFactory.getLogger(EventService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    private String getTimestamp(long epoch) {
        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.systemDefault()).format(formatter);
    }

    private List<Event> extract() {
        try {
            return eventRepository.extractAll();
        } catch (Exception e) {
            log.error("failed to extract events", e);
            throw new ExtractionException();
        }
    }

    private List<Event> extractSince(long since) {
        try {
            return eventRepository.extractSince(since);
        } catch (Exception e) {
            log.error("failed to extract events since {}", since, e);
            throw new ExtractionException();
        }
    }

    public List<Event> extract(Long since) {
        if (since == null) {
            return extract();
        }
        return extractSince(since);
    }

    public void create(long start, long end, Event.Action action, Event.Entity entity, String name, Object object) {
        String note;
        try {
            note = objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("failed to convert to JSON note of event {}", name, e);
            return;
        }
        String startTimestamp = getTimestamp(start);
        String endTimestamp = getTimestamp(end);
        long elapsed = end - start;
        String user = ""; //TODO: obtain username from current session
        Event event = new Event(start, startTimestamp, end, endTimestamp, elapsed, action, entity, name, user, note);
        try {
            eventRepository.create(event);
        } catch (Exception e) {
            log.error("failed to create event {}", event, e);
        }
    }
}