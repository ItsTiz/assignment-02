package it.unibo.pcd.assignment02.part2.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Node {
    public final String name;
    public final Set<String> dependencies = new HashSet<>();

    public Node(String name) {
        this.name = name;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    @Override
    public String toString(){
        return "Node: " + name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(name, node.name) && Objects.equals(dependencies, node.dependencies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dependencies);
    }
}
