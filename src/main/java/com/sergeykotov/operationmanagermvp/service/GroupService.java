package com.sergeykotov.operationmanagermvp.service;

import com.sergeykotov.operationmanagermvp.event.Event;
import com.sergeykotov.operationmanagermvp.event.EventService;
import com.sergeykotov.operationmanagermvp.exception.DatabaseException;
import com.sergeykotov.operationmanagermvp.exception.ExtractionException;
import com.sergeykotov.operationmanagermvp.exception.NotFoundException;
import com.sergeykotov.operationmanagermvp.model.Group;
import com.sergeykotov.operationmanagermvp.repository.GroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public List<Group> extractAll() {
        try {
            return groupRepository.extractAll();
        } catch (Exception e) {
            log.error("failed to extract groups", e);
            throw new ExtractionException();
        }
    }

    public Group extractById(long id) {
        Optional<Group> group;
        try {
            group = groupRepository.extractById(id);
        } catch (Exception e) {
            log.error("failed to extract group by ID: {}", id, e);
            throw new ExtractionException();
        }
        return group.orElseThrow(NotFoundException::new);
    }

    public void create(Group group) {
        log.info("creating group: {}", group);
        long start = System.currentTimeMillis();
        try {
            groupRepository.create(group);
        } catch (Exception e) {
            log.error("failed to create group: {}", group, e);
            throw new DatabaseException();
        }
        long end = System.currentTimeMillis();
        log.info("group created: {}", group);
        eventService.create(start, end, Event.Action.CREATED, Event.Entity.GROUP, group.toString(), group);
    }

    public void updateById(long id, Group group) {
        group.setId(id);
        Group currentGroup = extractById(id);
        String update = currentGroup.getName();
        if (!update.equals(group.getName())) {
            update = update + " -> " + group.getName();
        }
        log.info("updating group: {}", update);
        long start = System.currentTimeMillis();
        try {
            groupRepository.updateById(group);
        } catch (Exception e) {
            log.error("failed to update group: {}", update, e);
            throw new DatabaseException();
        }
        long end = System.currentTimeMillis();
        log.info("group updated: {}", update);
        eventService.create(start, end, Event.Action.UPDATED, Event.Entity.GROUP, group.toString(), group);
    }

    public void deleteById(long id) {
        Group group = extractById(id);
        log.info("deleting group: {}", group);
        long start = System.currentTimeMillis();
        try {
            groupRepository.deleteById(id);
        } catch (Exception e) {
            log.error("failed to delete group: {}", group, e);
            throw new DatabaseException();
        }
        long end = System.currentTimeMillis();
        log.info("group deleted: {}", group);
        eventService.create(start, end, Event.Action.DELETED, Event.Entity.GROUP, group.toString(), group);
    }
}