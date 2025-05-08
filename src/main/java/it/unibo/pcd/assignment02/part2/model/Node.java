package it.unibo.pcd.assignment02.part2.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Node {
    public final String name;
    public final String packageName;

    public Set<String> dependencies = new HashSet<>();

    public Node(String name, String packageName) {
        this.name = name;
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return name;
    }

    public void setDependencies(Set<String> dependencies) {
        this.dependencies = dependencies;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    @Override
    public String toString(){
        String deps = dependencies.isEmpty() ? "" : dependencies.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n  - ", "  - ", ""));

        return "[NODE]" + "\n"
                + "Name: " + name + "\n"
                + "Package: " + packageName + "\n"
                + (deps.isEmpty() ? "No dependencies.\n\n" : "Dependencies ("
                + dependencies.size() +"): \n" + deps);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(name, node.name) && Objects.equals(packageName, node.packageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, packageName);
    }
}
