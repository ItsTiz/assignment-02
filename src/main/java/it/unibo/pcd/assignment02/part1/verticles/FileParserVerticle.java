package it.unibo.pcd.assignment02.part1.verticles;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import it.unibo.pcd.assignment02.part1.utils.Errors;
import java.nio.file.Path;

public class FileParserVerticle extends AbstractVerticle {

    private final JavaParser parser;

    public FileParserVerticle(){
        this.parser = new JavaParser();
    }

    public void start() {
        vertx.eventBus().consumer("parser.file", this::parseFile);
    }

    private void parseFile(Message<JsonObject> message){
        JsonObject body = message.body();
        String filePath = body.getString("file");

        parseAst(filePath)
                .onSuccess(compilationUnit -> {
                    message.reply(convertCompilationUnitToJson(filePath, compilationUnit));
                })
                .onFailure(throwable -> {
                    message.fail(Errors.PARSING_ERROR.getCode(), "Error while parsing the file.");
                });
    }

    private JsonObject convertCompilationUnitToJson(String filePath, CompilationUnit cu) {
        return new JsonObject()
            .put("classPath", filePath)
            .put("packageDeclaration", cu.getPackageDeclaration().isPresent() ?
                cu.getPackageDeclaration().get().getNameAsString() : "")
            .put("className", cu.getPrimaryTypeName().isPresent() ? cu.getPrimaryTypeName().get() : "")
            .put("types", cu.getTypes().stream().map(e-> e.getName().asString()).toList())
            .put("imports", cu.getImports().stream().map(e -> e.getName().asString()).toList());
    }

    private Future<CompilationUnit> parseAst(String content) {
        return this.vertx.executeBlocking(() -> {
            try {
                return parser
                        .parse(Path.of(content))
                        .getResult()
                        .orElseThrow(() -> new Exception("Parsing failed"));
            } catch (Exception e) {
                throw new Exception("Error while trying to parse the file.");
            }
        });
    }
}