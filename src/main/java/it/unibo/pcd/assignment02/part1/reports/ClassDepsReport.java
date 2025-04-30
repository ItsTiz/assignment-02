package it.unibo.pcd.assignment02.part1.reports;
import com.github.javaparser.ast.CompilationUnit;
import io.vertx.core.json.JsonObject;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassDepsReport extends Report {
    private final Path classPath;
    private final String packageDeclaration;
    private final String topLevelType;;

    public ClassDepsReport(Path classPath, String packageDeclaration, String topLevelType, Set<String> types, Set<String> dependencies) {
        super(types, dependencies);
        this.classPath = classPath;
        this.packageDeclaration = packageDeclaration;
        this.topLevelType = topLevelType;
    }

    public static ClassDepsReport fromCompilationUnit(Path classPath, CompilationUnit unit) throws IllegalArgumentException {
        if(unit == null){
            throw new IllegalArgumentException("Parser root is invalid.");
        }

        return new ClassDepsReport(
                classPath,
                unit.getPackageDeclaration().isPresent() ? unit.getPackageDeclaration().get().getNameAsString() : "",
                unit.getPrimaryTypeName().isPresent() ? unit.getPrimaryTypeName().get() : "",
                new HashSet<>(unit.getTypes().stream().map(e -> e.getName().asString()).toList()),
                new HashSet<>(unit.getImports().stream().map(e -> e.getName().asString()).toList())
        );
    }

    public Path getClassPath() {
        return classPath;
    }

    public String getPackageDeclaration() {
        return packageDeclaration;
    }

    public String getTopLevelType() {
        return topLevelType;
    }
}
