package com.example.app.models;

import com.example.app.exceptions.ModelException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Index {

    private Long id;
    private Long index;
    private Long moduleId;
    private Long vacant;
    private Long waitlist;
    private HashSet<Session> sessions;

    public Index(Long moduleId, Long index) {
        this.index = index;
        this.moduleId = moduleId;
    }

    public Long getIndex() {
        return index;
    }

    public HashSet<Session> getSessions() {
        return sessions;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public void setSessions(HashSet<Session> sessions) throws ModelException {
        this.sessions = new HashSet<>(sessions);
    }

    public void addSession(Session session) throws ModelException {
        if (this.sessions == null) {
            this.sessions = new HashSet<>();
        }
        this.sessions.add(session);
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public Long getId() {
        return id;
    }

}