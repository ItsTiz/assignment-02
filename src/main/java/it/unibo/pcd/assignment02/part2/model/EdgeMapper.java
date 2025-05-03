package it.unibo.pcd.assignment02.part2.model;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.HashSet;
import java.util.stream.Collectors;

import static it.unibo.pcd.assignment02.part2.utils.Util.extractName;

public class EdgeMapper {

    EdgeMapper(){}

    public Function<ParsedJavaFile, Observable<Edge>> flattenToEdges() {
        return e -> {
            String nodeName = e.getFileName();
            if(nodeName.isEmpty()){
                nodeName = extractName(e.getFilePath(), "\\\\");
            }
            Node startNode = new Node(nodeName);
            HashSet<Edge> temp = new HashSet<>();
            if(!e.getDependencies().isEmpty()) {
                temp = (HashSet<Edge>) e.getDependencies()
                        .stream()
                        .filter(str -> str.contains("."))
                        .map(s -> new Edge(startNode, new Node(extractName(s, "\\."))))
                        .collect(Collectors.toSet());

            }
            return Observable.fromIterable(temp).subscribeOn(Schedulers.computation());
        };
    }
}
