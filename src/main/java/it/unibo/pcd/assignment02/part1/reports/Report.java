package it.unibo.pcd.assignment02.part1.reports;

import java.util.Set;

public abstract class Report {
    private final Set<String> types;
    private final Set<String> dependencies;

    protected Report(Set<String> types, Set<String> dependencies) {
        this.types = types;
        this.dependencies = dependencies;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    public Set<String> getTypes() {
        return types;
    }
}
