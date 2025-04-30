package it.unibo.pcd.assignment02.part1.reports;

import io.vertx.core.json.JsonObject;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PackageDepsReport extends Report{
    private final Path packagePath;

    public PackageDepsReport(Path packagePath, Set<String> types, Set<String> dependencies) {
        super(types, dependencies);
        this.packagePath = packagePath;
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
}
