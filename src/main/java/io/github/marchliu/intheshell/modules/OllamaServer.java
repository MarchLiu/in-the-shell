package io.github.marchliu.intheshell.modules;

import io.github.marchliu.intheshell.TheShellApplication;
import jaskell.util.Failure;
import jaskell.util.Success;
import jaskell.util.Try;
import javafx.concurrent.Task;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;

public class OllamaServer extends Server {
    public OllamaServer() {
        super();
    }

    public OllamaServer(String host, int port) {
        super(host, port);
    }

    public List<String> models() throws Exception {
        Context context = TheShellApplication.context();
        URI uri = new URI("http://%s:%d/api/tags".formatted(host, port));
        var request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .GET()
                .build();
        var resp = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .join();
        var body = resp.body();
        List<String> result = new ArrayList<>();
        var node = context.jsonUtils.toNode(body).get();
        var models = node.get("models");
        for (int idx = 0; idx < models.size(); idx++) {
            result.add(models.get(idx).get("name").asText());
        }
        return result;
    }


    public String embed(String content) {
        return "";
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<Try<Response>> talk(Request request, String model, String system) {
        Context context = TheShellApplication.context();
        Map<String, Object> req = new HashMap<>();
        req.put("prompt", request.getContent());
        req.put("stream", false);
        req.put("system", system);
        req.put("model", model);
        req.put("context", request.getContext());

        var tryFuture = context.jsonUtils.writeToString(req).map(body -> {
            try (var client = context.httpClient()) {
                var uri = new URI("http://%s:%d/api/generate".formatted(host, port));
                var httpRequest = HttpRequest.newBuilder()
                        .uri(uri)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();

                return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                        .thenApply(resp -> context.getJsonUtils().toMap(resp.body())
                                .map(m -> new Response(((String) m.get("response")).strip(),
                                        (List<Integer>) m.get("context"))));
            }
        });
        return switch (tryFuture) {
            case Success(var future) -> future;
            case Failure(var err) -> CompletableFuture.failedFuture(err);
        };
    }

    public Try<Iterator<String>> stream(String input, String model, String system) {
        return Try.success(new Iterator<>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public String next() {
                return null;
            }
        });
    }

    @Override
    public Server clone() {
        return new OllamaServer(this.host, this.port);
    }
}
