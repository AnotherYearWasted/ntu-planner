package com.example.app.models;

public class Class extends Module {

    public enum ClassType {
        LECTURE,
        TUTORIAL,
        LAB,
        SEMINAR
    }

    private Long index;
    private Long startHour;
    private Long endHour;
    private ClassType classType;
    private String venue;
    private String group;

    public Class() {
    }

    public Class(ClassBuilder builder) {
        this.index = builder.index;
        this.startHour = builder.startHour;
        this.endHour = builder.endHour;
        this.classType = builder.classType;
        this.venue = builder.venue;
        this.group = builder.group;
    }

    public Long getStartHour() {
        return startHour;
    }

    public Long getEndHour() {
        return endHour;
    }

    public ClassType getClassType() {
        return classType;
    }

    public String getVenue() {
        return venue;
    }

    public String getGroup() {
        return group;
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public void setStartHour(Long startHour) {
        this.startHour = startHour;
    }

    public void setEndHour(Long endHour) {
        this.endHour = endHour;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public static ClassBuilder builder() {
        return new ClassBuilder();
    }

    public static class ClassBuilder extends ModuleBuilder {
        private Long index;
        private Long startHour;
        private Long endHour;
        private ClassType classType;
        private String venue;
        private String group;

        public ClassBuilder setIndex(Long index) {
            this.index = index;
            return this;
        }

        public ClassBuilder setStartHour(Long startHour) {
            this.startHour = startHour;
            return this;
        }

        public ClassBuilder setEndHour(Long endHour) {
            this.endHour = endHour;
            return this;
        }

        public ClassBuilder setClassType(ClassType classType) {
            this.classType = classType;
            return this;
        }

        public ClassBuilder setVenue(String venue) {
            this.venue = venue;
            return this;
        }

        public ClassBuilder setGroup(String group) {
            this.group = group;
            return this;
        }

        public Class build() {
            return new Class(this);
        }
    }

}
