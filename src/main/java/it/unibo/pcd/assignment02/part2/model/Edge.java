package it.unibo.pcd.assignment02.part2.model;

public class Edge {

    private final Node fromNode;
    private final Node toNode;

    Edge(final Node from, final Node to) {
        this.fromNode = from;
        this.toNode = to;
    }

    public Node getToNode() {
        return toNode;
    }

    public Node getFromNode() {
        return fromNode;
    }

    @Override
    public String toString(){
        return "\nEdge: \n" +
                fromNode + " ---> " + toNode + "\n";
    }


}
