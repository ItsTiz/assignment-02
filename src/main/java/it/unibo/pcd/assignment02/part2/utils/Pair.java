package it.unibo.pcd.assignment02.part2.utils;

public class Pair<A, B> {
    public final A first;
    public final B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A first(){
        return first;
    }

    public B second(){
        return second;
    }
}

