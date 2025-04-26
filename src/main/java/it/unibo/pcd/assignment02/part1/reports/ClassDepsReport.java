package it.unibo.pcd.assignment02.part1.reports;
import io.vertx.core.json.JsonObject;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassDepsReport {
    private final Path classPath;
    private final String packageDeclaration;
    private final String topLevelType;
    private final Set<String> types;
    private final Set<String> dependencies;

    public ClassDepsReport(Path classPath, String packageDeclaration, String topLevelType, Set<String> types, Set<String> dependencies) {
        this.classPath = classPath;
        this.packageDeclaration = packageDeclaration;
        this.topLevelType = topLevelType;
        this.types = types;
        this.dependencies = dependencies;
    }

    public static ClassDepsReport fromJson(JsonObject analysedData) throws IllegalArgumentException {
        if(analysedData.isEmpty()){
            throw new IllegalArgumentException("Json data object is empty.");
        }

        JsonObject reportJson = analysedData.getJsonObject("classReport");

        return new ClassDepsReport(
                Path.of(reportJson.getString("classPath")),
                reportJson.getString("packageDeclaration"),
                reportJson.getString("className"),
                reportJson.getJsonArray("types").stream().map(Object::toString).collect(Collectors.toSet()),
                reportJson.getJsonArray("dependencies").stream().map(Object::toString).collect(Collectors.toSet())
        );
    }

    public Path getClassPath() {
        return classPath;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    public String getPackageDeclaration() {
        return packageDeclaration;
    }

    public String getTopLevelType() {
        return topLevelType;
    }

    public Set<String> getTypes() {
        return types;
    }
}
