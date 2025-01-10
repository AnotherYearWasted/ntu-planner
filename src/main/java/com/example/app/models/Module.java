package com.example.app.models;

import java.util.HashSet;

public class Module {
    private final Long id;
    private String name;
    private String description;
    private String moduleType;
    private String moduleCode;
    private Float credits;
    private HashSet<String> prerequisites;

    public Module() {
        this.id = null;
    }

    public Module(ModuleBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.moduleType = builder.moduleType;
        this.moduleCode = builder.moduleCode;
        this.credits = builder.credits;
        this.prerequisites = builder.prerequisites;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getModuleType() {
        return moduleType;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public Float getCredits() {
        return credits;
    }

    public HashSet<String> getPrerequisites() {
        return prerequisites;
    }

    public Module setName(String name) {
        this.name = name;
        return this;
    }

    public Module setDescription(String description) {
        this.description = description;
        return this;
    }

    public Module setModuleType(String moduleType) {
        this.moduleType = moduleType;
        return this;
    }

    public Module setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
        return this;
    }

    public Module setCredits(Float moduleCredits) {
        this.credits = moduleCredits;
        return this;
    }

    public Module setPrerequisites(HashSet<String> prerequisites) {
        this.prerequisites = prerequisites;
        return this;
    }

    public Module addPrerequisite(String prerequisite) {
        if (prerequisites == null) {
            prerequisites = new HashSet<>();
        }
        prerequisites.add(prerequisite);
        return this;
    }

    public Module removePrerequisite(String prerequisite) {
        if (prerequisites != null) {
            prerequisites.remove(prerequisite);
        }
        return this;
    }

    public static ModuleBuilder builder() {
        return new ModuleBuilder();
    }

    public static class ModuleBuilder {
        private Long id;
        private String name;
        private String description;
        private String moduleType;
        private String moduleCode;
        private Float credits;
        private HashSet<String> prerequisites;

        public ModuleBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public ModuleBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public ModuleBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public ModuleBuilder setModuleType(String moduleType) {
            this.moduleType = moduleType;
            return this;
        }

        public ModuleBuilder setModuleCode(String moduleCode) {
            this.moduleCode = moduleCode;
            return this;
        }

        public ModuleBuilder setCredits(Float credits) {
            this.credits = credits;
            return this;
        }

        public ModuleBuilder setPrerequisites(HashSet<String> prerequisites) {
            this.prerequisites = prerequisites;
            return this;
        }

        public ModuleBuilder addPrerequisite(String prerequisite) {
            if (prerequisites == null) {
                prerequisites = new HashSet<>();
            }
            prerequisites.add(prerequisite);
            return this;
        }

        public Module build() {
            return new Module(this);
        }
    }

}
