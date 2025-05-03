package it.unibo.pcd.assignment02.part2.model;

import java.util.Set;
import java.util.stream.Collectors;

public class ParsedJavaFile {

    private final String filePath;

    private final String fileName;
    private final String packageName;
    private final Set<String> dependencies;
    public ParsedJavaFile(String filePath, String fileName, String packageName, Set<String> dependencies) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.packageName = packageName;
        this.dependencies = dependencies;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getPackageName() {
        return packageName;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString(){
        String deps = dependencies.isEmpty() ? "" : dependencies.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n  - ", "  - ", ""));
        return "\n"
                + "File: " + fileName + "\n"
                + "Package: " + packageName + "\n"
                + (deps.isEmpty() ? "No dependencies.\n\n" : "Dependencies: \n" + deps
                + "\n\n");
    }

}
