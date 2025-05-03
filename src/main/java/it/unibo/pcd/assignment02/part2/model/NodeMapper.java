package it.unibo.pcd.assignment02.part2.model;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.HashSet;
import java.util.stream.Collectors;

import static it.unibo.pcd.assignment02.part2.utils.Util.extractName;

public class NodeMapper {

    NodeMapper(){}

    public Function<ParsedJavaFile, Observable<Node>> flattenToNodes() {
        return e -> {
            HashSet<Node> temp =
                    (HashSet<Node>) e.getDependencies()
                    .stream()
                    .filter(str -> !str.contains("."))
                    .map(s -> new Node(extractName(s, ".")))
                    .collect(Collectors.toSet());
            String nodeName = e.getFileName();
            if(nodeName.isEmpty()){
                nodeName = extractName(e.getFilePath(), "\\\\");
            }
            temp.add(new Node(nodeName));
            return Observable.fromIterable(temp).subscribeOn(Schedulers.computation());
        };
    }
}
