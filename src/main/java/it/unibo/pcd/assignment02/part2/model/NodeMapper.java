package it.unibo.pcd.assignment02.part2.model;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.unibo.pcd.assignment02.part2.utils.Util;

import java.util.HashSet;
import java.util.stream.Collectors;

import static it.unibo.pcd.assignment02.part2.utils.Util.extractName;
import static it.unibo.pcd.assignment02.part2.utils.Util.extractPackageName;

public class NodeMapper {

    NodeMapper(){}

    public Function<ParsedJavaFile, Observable<Node>> flattenToNodes() {
        return e -> {
            HashSet<Node> temp =
                    (HashSet<Node>) e.getDependencies()
                    .stream()
                    .filter(str -> str.contains("."))
                    .map(s -> new Node(extractName(s, "\\."), extractPackageName(s)))
                    .collect(Collectors.toSet());
            String nodeName = e.getFileName();
            if(nodeName.isEmpty()){
                nodeName = extractName(e.getFilePath(), "\\\\");
            }
            Node mainNode = new Node(nodeName, e.getPackageName());
            mainNode.setDependencies(e.getDependencies());
           // Util.err("node: "+mainNode + "parsedjavafile: "+e);
            temp.add(mainNode);
            return Observable.fromIterable(temp).subscribeOn(Schedulers.computation());
        };
    }
}
