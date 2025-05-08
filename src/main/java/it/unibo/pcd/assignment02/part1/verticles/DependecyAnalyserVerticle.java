package it.unibo.pcd.assignment02.part1.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import it.unibo.pcd.assignment02.part1.utils.Errors;

import java.util.HashSet;
import java.util.Set;

public class DependecyAnalyserVerticle extends AbstractVerticle {

    public void start() {
        vertx.eventBus().consumer("dependency.analyse", this::analyseDependencies);
    }

    private void analyseDependencies(Message<JsonObject> message) {
        JsonObject body = message.body();
        JsonObject astJson = body.getJsonObject("ast");

        if(!astJson.isEmpty()) {
            JsonObject result = generateMessageReply(astJson);
            message.reply(result);
        }else{
            //message.fail(Errors.ANALYSER_ERROR.getCode(), "Failed to analyse parsing result.");
        }
    }

    private JsonObject generateMessageReply(JsonObject astJson) {
        String classPath = astJson.getString("classPath");
        String packageDeclaration = astJson.getString("packageDeclaration");
        String className = astJson.getString("className");
        Set<String> types = toStringSet(astJson.getJsonArray("types"));
        Set<String> dependencies = toStringSet(astJson.getJsonArray("imports"));

        return JsonObject.of().put("classReport", convertAstResultsToJson(
                classPath,
                packageDeclaration,
                className,
                types,
                dependencies
        ));
    }

    private Set<String> toStringSet(JsonArray array) {
        return new HashSet<>(array.stream().map(Object::toString).toList());
    }

    private JsonObject convertAstResultsToJson(
            String classPath,
            String packageDeclaration,
            String className,
            Set<String> types,
            Set<String> dependencies
    ) {
        return new JsonObject()
                .put("classPath", classPath)
                .put("packageDeclaration", packageDeclaration)
                .put("className", className)
                .put("types", types.stream().toList())
                .put("dependencies", dependencies.stream().toList());
    }
}