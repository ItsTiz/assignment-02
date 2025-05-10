package it.unibo.pcd.assignment02.part2.model;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.HashSet;
import java.util.Set;

import static it.unibo.pcd.assignment02.part2.utils.Util.extractName;
import static it.unibo.pcd.assignment02.part2.utils.Util.extractPackageName;

public class NodeMapper {

    private HashSet<String> projectFileNames;

    public NodeMapper(){}

    public void setProjectFileNames(HashSet<String> projectFileNames) {
        this.projectFileNames = projectFileNames;
    }

    public Function<ParsedJavaFile, Observable<Node>> flattenToNodes() {
        return e -> {
            Set<Node> result = new HashSet<>();

            String nodeName = e.getFileName();
            if (nodeName.isEmpty()) {
                nodeName = extractName(e.getFilePath(), "\\\\");
            }

            Node mainNode = new Node(nodeName, e.getPackageName());

            mainNode.setDependencies(e.getDependencies());

            result.add(mainNode);

            e.getDependencies().stream()
                    .filter(str -> str.contains(".") && !projectFileNames.contains(str))
                    .forEach(dep -> {
                        String depName = extractName(dep, "\\.");
                        String depPackage = extractPackageName(dep);
                        Node depNode = new Node(depName, depPackage);
                        result.add(depNode);
                    });

            return Observable.fromIterable(result).subscribeOn(Schedulers.computation());
        };
    }
}
