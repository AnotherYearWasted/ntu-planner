package com.example.app.models;

import com.example.app.exceptions.BuilderException;
import com.example.app.exceptions.ModelException;

import java.util.HashMap;
import java.util.HashSet;

public class Module {
    private final Long id;
    private String name;
    private String description;
    private String moduleType;
    private String moduleCode;
    private Float credits;
    private HashSet<String> prerequisites;
    private HashMap<Long, Index> indexes;

    private Module() {
        this.id = null;
        this.prerequisites = new HashSet<>();
        this.indexes = new HashMap<>();
    }

    private Module(ModuleBuilder builder) throws ModelException {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.moduleType = builder.moduleType;
        this.moduleCode = builder.moduleCode;
        this.credits = builder.credits;
        this.prerequisites = builder.prerequisites != null ? new HashSet<>(builder.prerequisites) : new HashSet<>();
        this.indexes = builder.indexes != null ? new HashMap<>(builder.indexes) : new HashMap<>();
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

    public HashMap<Long, Index> getIndexes() {
        return indexes;
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
        this.prerequisites = new HashSet<>(prerequisites);
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

    public Module setIndexes(HashMap<Long, Index> indexes) {
        this.indexes = new HashMap<>(indexes);
        return this;
    }

    public Module addIndex(Index index) throws ModelException {
        if (index == null) {
            throw new ModelException("Index cannot be null");
        }
        if (indexes == null) {
            indexes = new HashMap<>();
        }
        indexes.put(index.getIndex(), index);
        return this;
    }

    public Module removeIndex(Index index) {
        if (index != null) {
            indexes.remove(index.getIndex());
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
        private HashMap<Long, Index> indexes;

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
            this.prerequisites = new HashSet<>(prerequisites);
            return this;
        }

        public ModuleBuilder addPrerequisite(String prerequisite) {
            if (prerequisites == null) {
                prerequisites = new HashSet<>();
            }
            prerequisites.add(prerequisite);
            return this;
        }

        public ModuleBuilder removePrerequisite(String prerequisite) {
            if (prerequisites != null) {
                prerequisites.remove(prerequisite);
            }
            return this;
        }

        public ModuleBuilder setIndexes(HashMap<Long, Index> indexes) {
            this.indexes = new HashMap<>(indexes);
            return this;
        }

        public ModuleBuilder addIndex(Index index) throws BuilderException {
            if (index == null) {
                throw new BuilderException("Index cannot be null");
            }
            if (indexes == null) {
                indexes = new HashMap<>();
            }
            indexes.put(index.getIndex(), index);
            return this;
        }

        public ModuleBuilder removeIndex(Index index) {
            if (indexes != null) {
                indexes.remove(index.getIndex());
            }
            return this;
        }

        public Module build() throws BuilderException {
            try {
                return new Module(this);
            } catch (ModelException e) {
                throw new BuilderException(e);
            }
        }
    }

}
