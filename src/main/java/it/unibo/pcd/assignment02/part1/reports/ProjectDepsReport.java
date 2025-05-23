package it.unibo.pcd.assignment02.part1.reports;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProjectDepsReport extends Report {
    private final Path projectPath;

    public ProjectDepsReport(Path projectPath, Set<String> types, Set<String> dependencies) {
        super(types, dependencies);
        this.projectPath = projectPath;
    }

    public static ProjectDepsReport emptyProjectReport(Path projectPath) {
        return new ProjectDepsReport(projectPath, new HashSet<>(), new HashSet<>());
    }

    public static ProjectDepsReport fromPackageReportList(Path projectPath, List<PackageDepsReport> packageReports) throws IllegalArgumentException {
        if (packageReports.isEmpty()) {
            throw new IllegalArgumentException("There are no package reports for the project.");
        }

        Set<String> types = new HashSet<>();
        Set<String> dependencies = new HashSet<>();

        for (PackageDepsReport packageReport : packageReports) {
            types.addAll(packageReport.getTypes());
            dependencies.addAll(packageReport.getDependencies());
        }

        return new ProjectDepsReport(projectPath, types, dependencies);
    }

    public Path getProjectPath() {
        return projectPath;
    }
}
