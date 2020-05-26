package com.sergeykotov.operationmanagermvp.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergeykotov.operationmanagermvp.exception.ExtractionException;
import com.sergeykotov.operationmanagermvp.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

@Service
public class EventService {
    private static final Logger log = LoggerFactory.getLogger(EventService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> extract() {
        try {
            return eventRepository.extract();
        } catch (Exception e) {
            log.error("failed to extract events", e);
            throw new ExtractionException();
        }
    }

    public List<Event> extract(long since) {
        try {
            return eventRepository.extract(since);
        } catch (Exception e) {
            log.error("failed to extract events since {}", since, e);
            throw new ExtractionException();
        }
    }

    public void create(long start, long end, Event.Action action, Event.Entity entity, String name, Object object) {
        String note;
        try {
            note = objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("failed to convert to JSON note of event {}", name, e);
            return;
        }
        String user = ""; //TODO: obtain username from current session
        LocalDateTime startTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(start), TimeZone.getDefault().toZoneId());
        LocalDateTime endTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(end), TimeZone.getDefault().toZoneId());
        Event event = new Event(start, startTime, end, endTime, end - start, action, entity, name, user, note);
        try {
            eventRepository.create(event);
        } catch (Exception e) {
            log.error("failed to create event {}", event, e);
        }
    }
}