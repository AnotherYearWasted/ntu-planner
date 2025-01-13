package com.example.app.models;

import com.example.app.exceptions.ModelException;

import java.util.HashSet;

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

    public Long getId() {
        return id;
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

    public Long getVacant() {
        return vacant;
    }

    public Long getWaitlist() {
        return waitlist;
    }

    public Index setIndex(Long index) {
        this.index = index;
        return this;
    }

    public Index setSessions(HashSet<Session> sessions) throws ModelException {
        this.sessions = new HashSet<>(sessions);
        return this;
    }

    public Index addSession(Session session) throws ModelException {
        if (this.sessions == null) {
            this.sessions = new HashSet<>();
        }
        this.sessions.add(session);
        return this;
    }

    public Index setModuleId(Long moduleId) {
        this.moduleId = moduleId;
        return this;
    }

    public Index setVacant(Long vacant) {
        this.vacant = vacant;
        return this;
    }

    public Index setWaitlist(Long waitlist) {
        this.waitlist = waitlist;
        return this;
    }

}