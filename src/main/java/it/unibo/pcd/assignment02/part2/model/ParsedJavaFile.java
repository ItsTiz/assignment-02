package it.unibo.pcd.assignment02.part2.model;

import java.util.Set;

public class ParsedJavaFile {

    private final String fileName;
    private final String packageName;
    private final Set<String> dependencies;

    public ParsedJavaFile(String fileName, String packageName, Set<String> dependencies) {
        this.fileName = fileName;
        this.packageName = packageName;
        this.dependencies = dependencies;
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



}
