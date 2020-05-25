package com.sergeykotov.operationmanagermvp.service;

import com.sergeykotov.operationmanagermvp.event.Event;
import com.sergeykotov.operationmanagermvp.event.EventService;
import com.sergeykotov.operationmanagermvp.exception.DatabaseException;
import com.sergeykotov.operationmanagermvp.exception.ExtractionException;
import com.sergeykotov.operationmanagermvp.model.Group;
import com.sergeykotov.operationmanagermvp.repository.GroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {
    private static final Logger log = LoggerFactory.getLogger(GroupService.class);

    private final EventService eventService;
    private final GroupRepository groupRepository;

    @Autowired
    public GroupService(EventService eventService, GroupRepository groupRepository) {
        this.eventService = eventService;
        this.groupRepository = groupRepository;
    }

    public List<Group> extract() {
        try {
            return groupRepository.extract();
        } catch (Exception e) {
            log.error("failed to extract groups", e);
            throw new ExtractionException();
        }
    }

    public void create(Group group) {
        log.info("creating group {}", group);
        long start = System.currentTimeMillis();
        try {
            groupRepository.create(group);
        } catch (Exception e) {
            log.error("failed to create group {}", group, e);
            throw new DatabaseException();
        }
        long end = System.currentTimeMillis();
        log.info("group {} created", group);
        eventService.create(start, end, Event.Action.CREATED, Event.Entity.GROUP, group.toString(), group);
    }

    public void update(Group group) {
        log.info("updating group {}", group);
        long start = System.currentTimeMillis();
        try {
            groupRepository.update(group);
        } catch (Exception e) {
            log.error("failed to update group {}", group, e);
            throw new DatabaseException();
        }
        long end = System.currentTimeMillis();
        log.info("group {} updated", group);
        eventService.create(start, end, Event.Action.UPDATED, Event.Entity.GROUP, group.toString(), group);
    }

    public void delete(Group group) {
        log.info("deleting group {}", group);
        long start = System.currentTimeMillis();
        try {
            groupRepository.delete(group.getId());
        } catch (Exception e) {
            log.error("failed to delete group {}", group, e);
            throw new DatabaseException();
        }
        long end = System.currentTimeMillis();
        log.info("group {} deleted", group);
        eventService.create(start, end, Event.Action.DELETED, Event.Entity.GROUP, group.toString(), group);
    }
}