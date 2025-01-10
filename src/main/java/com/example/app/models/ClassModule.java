package com.example.app.models;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class ClassModule {
    public enum ClassType {
        LECTURE,
        TUTORIAL,
        LAB,
        SEMINAR,
        UNKNOWN
    }

    private Long index;
    @JsonIgnore
    private Module module;
    private Session session;
    private ClassType classType;
    private String venue;
    private String group;
    private String remark;

    public ClassModule() {
        this.session = new Session();
    }

    private ClassModule(ClassBuilder builder) {
        this.index = builder.index;
        this.module = builder.module;
        this.session = builder.session;
        this.classType = builder.classType;
        this.venue = builder.venue;
        this.group = builder.group;
        this.remark = builder.remark;
    }

    public Session getSession() {
        return session;
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

    public Module getModule() {
        return module;
    }

    public String getRemark() {
        return remark;
    }

    public void setIndex(Long index) {
        this.index = index;
    }


    public void setSession(Session session) {
        this.session = session;
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

    public void setModule(Module module) {
        this.module = module;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public static ClassBuilder builder() {
        return new ClassBuilder();
    }

    public static class ClassBuilder {
        private Long index;
        private Module module;
        private Session session;
        private ClassType classType;
        private String venue;
        private String group;
        private String remark;

        public ClassBuilder setIndex(Long index) {
            this.index = index;
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

        public ClassBuilder setModule(Module module) {
            this.module = module;
            return this;
        }

        public ClassBuilder setSession(Session session) {
            this.session = session;
            return this;
        }

        public ClassBuilder setRemark(String remark) {
            this.remark = remark;
            return this;
        }

        public ClassModule build() {
            return new ClassModule(this);
        }
    }

}
