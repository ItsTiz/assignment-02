package it.unibo.pcd.assignment02.part1;

import io.vertx.core.Future;
import it.unibo.pcd.assignment02.part1.reports.ClassDepsReport;
import it.unibo.pcd.assignment02.part1.reports.PackageDepsReport;
import it.unibo.pcd.assignment02.part1.reports.ProjectDepsReport;

import java.nio.file.Path;

public interface DependencyAnalyserLib {
    /** Asynchronously analyzes the dependencies of the specified class source file.
     *
     *
     * This method processes the provided class file and produces a report that includes the list of types
     * (classes or interfaces) used or accessed by the given file. The result is returned as an
     * instance of {@code ClassDepsReport}.
     *
     * @param classSrcFile the path of the file to analyze
     * @return a future of type {@code ClassDepsReport} containing the list of dependencies for the entire file
     * @throws NullPointerException if the specified path file is null
     * @throws IllegalArgumentException if the specified path file is invalid or does not exist
     */
    Future<ClassDepsReport> getClassDependencies(Path classSrcFile);

    /**
     * Asynchronously analyzes the dependencies of all classes or interfaces contained within the specified package source folder.
     *
     * This method processes the provided package source folder and produces a report that includes the list of types
     * (classes or interfaces) used or accessed by any class or interface in the package. The result is returned as an
     * instance of {@code PackageDepsReport}.
     *
     * @param packageSrcFolder the source folder of the package to analyze
     * @return a future of type {@code PackageDepsReport} containing the list of dependencies for the entire package
     * @throws NullPointerException if the specified package source folder is null
     * @throws IllegalArgumentException if the specified package source folder is invalid or does not exist
     */
    Future<PackageDepsReport> getPackageDependencies(Path packageSrcFolder);

    /**
     * Asynchronously analyzes the dependencies of all classes or interfaces contained within the specified project source folder.
     *
     * This method processes the provided project source folder and produces a report that includes the list of types
     * (classes or interfaces) used or accessed by any class or interface in the entire project. The result is returned as
     * an instance of {@code ProjectDepsReport}.
     *
     * @param projectSrcFolder the source folder of the project to analyze
     * @return a future of type {@code ProjectDepsReport} containing the list of dependencies for the entire project
     * @throws NullPointerException if the specified project source folder is null
     * @throws IllegalArgumentException if the specified project source folder is invalid or does not exist
     */
    Future<ProjectDepsReport> getProjectDependencies(Path projectSrcFolder);
}
