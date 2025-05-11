package it.unibo.pcd.assignment02.part2.model;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import io.reactivex.rxjava3.functions.Function;
import it.unibo.pcd.assignment02.part2.utils.Util;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class FileAnalyser {

    private final JavaParser parser;
    private final ParserConfiguration config;

    FileAnalyser(){
        config = new ParserConfiguration();
        config.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21);
        this.parser = new JavaParser(config);
    }

    public Function<File, ParsedJavaFile> dependenciesMapper() {
        return file -> {
            ParseResult<CompilationUnit> result = parser.parse(file);
            String fileName = "";
            String packageName = "";
            Set<String> dependencies = new HashSet<>();
            if(result.isSuccessful() && result.getResult().isPresent()){
                CompilationUnit cu = result.getResult().get();
                fileName = cu.getPrimaryType().isPresent() ? cu.getPrimaryType().get().getName().asString() : "[Unknown]";
                packageName = cu.getPackageDeclaration().isPresent() ? cu.getPackageDeclaration().get().getName().asString() : "[Unknown]";
                dependencies = cu.getImports().stream().map(e-> e.getName().asString()).collect(Collectors.toSet());
            }else {
                Util.log("Some errors occurred upon parsing: " + result.getProblems());
            }


            return new ParsedJavaFile(file.toString(), fileName, packageName, dependencies);
        };
    }

}
