package it.unibo.pcd.assignment02.part1.reports;

import io.vertx.core.json.JsonObject;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PackageDepsReport {
    private final Path packagePath;
    private final Set<String> types;
    private final Set<String> dependencies;

    public PackageDepsReport(Path packagePath, Set<String> types, Set<String> dependencies) {
        this.packagePath = packagePath;
        this.types = types;
        this.dependencies = dependencies;
    }

    public static PackageDepsReport emptyPackageReport(Path classPath){
        return new PackageDepsReport(classPath, new HashSet<>(), new HashSet<>());
    }

    public static PackageDepsReport fromClassReportList(Path packagePath, List<ClassDepsReport> classDepsReports) throws IllegalArgumentException{
        if(classDepsReports.isEmpty()) throw new IllegalArgumentException("There are no class reports for the package.");

        HashSet<String> types = new HashSet<>();
        HashSet<String> dependencies = new HashSet<>();
        for (ClassDepsReport classDepsReport : classDepsReports) {
            types.addAll(classDepsReport.getTypes());
            dependencies.addAll(classDepsReport.getDependencies());
        }

        return new PackageDepsReport(packagePath, types, dependencies);
    }

    public Path getPackagePath() {
        return packagePath;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    public Set<String> getTypes() {
        return types;
    }
}
