package io.github.marchliu.intheshell.modules;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Context {
    final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
    final Templates templates = Templates.service();
    final JsonUtils jsonUtils = new JsonUtils();

    public Template template(String tmpl) {
        return templates.getTemplate(tmpl);
    }

    public JsonUtils getJsonUtils() {
        return new JsonUtils();
    }

    public Executor getExecutor() {
        return executor;
    }

    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .executor(executor)
                .build();
    }


}
