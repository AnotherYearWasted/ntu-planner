package com.example.app.models;

import com.example.app.exceptions.BuilderException;
import com.example.app.exceptions.ModelException;

import java.time.DayOfWeek;

public class Session {

    public enum SessionType {
        LECTURE,
        TUTORIAL,
        LAB,
        SEMINAR,
        UNKNOWN
    }

    private Long indexId;
    private DayOfWeek day;
    private Long startHour;
    private Long startMinute;
    private Long endHour;
    private Long endMinute;
    private SessionType sessionType;
    private String venue;
    private String group;
    private String remark;

    private Session() throws ModelException {

    }

    private Session(DayOfWeek day, Long startHour, Long startMinute, Long endHour, Long endMinute) throws ModelException {
        this.day = day;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
    }

    private Session(SessionBuilder builder) throws ModelException {
        this.indexId = builder.indexId;
        this.day = builder.day;
        this.startHour = builder.startHour;
        this.startMinute = builder.startMinute;
        this.endHour = builder.endHour;
        this.endMinute = builder.endMinute;
        this.sessionType = builder.sessionType;
        this.venue = builder.venue;
        this.group = builder.group;
        this.remark = builder.remark;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public Long getStartHour() {
        return startHour;
    }

    public Long getStartMinute() {
        return startMinute;
    }

    public Long getEndHour() {
        return endHour;
    }

    public Long getEndMinute() {
        return endMinute;
    }

    public Long getIndexId() {
        return indexId;
    }

    public String getVenue() {
        return venue;
    }

    public String getGroup() {
        return group;
    }

    public String getRemark() {
        return remark;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    public void setStartHour(Long startHour) {
        this.startHour = startHour;
    }

    public void setStartMinute(Long startMinute) {
        this.startMinute = startMinute;
    }

    public void setEndHour(Long endHour) {
        this.endHour = endHour;
    }

    public void setEndMinute(Long endMinute) {
        this.endMinute = endMinute;
    }

    public void setIndexId(Long indexId) {
        this.indexId = indexId;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    public boolean isOverlap(Session other) throws ModelException {
        if (!this.day.equals(other.day)) {
            return false;
        }

        if (this.startHour > other.endHour || this.endHour < other.startHour) {
            return false;
        }

        if (this.startHour.equals(other.endHour) && this.startMinute >= other.endMinute) {
            return false;
        }

        return this.endHour.equals(other.startHour) || this.endMinute > other.startMinute;
    }

    public boolean isOverlap(Long startHour, Long startMinute, Long endHour, Long endMinute) throws ModelException {
        return isOverlap(new Session(day, startHour, startMinute, endHour, endMinute));
    }

    public static class SessionBuilder {

        private Long indexId;
        private DayOfWeek day;
        private Long startHour;
        private Long startMinute;
        private Long endHour;
        private Long endMinute;
        private SessionType sessionType;
        private String venue;
        private String group;
        private String remark;

        public SessionBuilder() throws BuilderException {
        }

        public SessionBuilder setIndexId(Long indexId) {
            this.indexId = indexId;
            return this;
        }

        public SessionBuilder setDay(DayOfWeek day) {
            this.day = day;
            return this;
        }

        public SessionBuilder setStartHour(Long startHour) {
            this.startHour = startHour;
            return this;
        }

        public SessionBuilder setStartMinute(Long startMinute) {
            this.startMinute = startMinute;
            return this;
        }

        public SessionBuilder setEndHour(Long endHour) {
            this.endHour = endHour;
            return this;
        }

        public SessionBuilder setEndMinute(Long endMinute) {
            this.endMinute = endMinute;
            return this;
        }

        public SessionBuilder setSessionType(SessionType sessionType) {
            this.sessionType = sessionType;
            return this;
        }

        public SessionBuilder setVenue(String venue) {
            this.venue = venue;
            return this;
        }

        public SessionBuilder setGroup(String group) {
            this.group = group;
            return this;
        }

        public SessionBuilder setRemark(String remark) {
            this.remark = remark;
            return this;
        }

        public Session build() throws BuilderException {
            try {
                return new Session(this);
            } catch (ModelException e) {
                throw new BuilderException(e);
            }
        }

    }

}
