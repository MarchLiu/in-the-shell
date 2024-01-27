package io.github.marchliu.intheshell.modules;

import io.github.marchliu.intheshell.TheShellApplication;
import jaskell.util.Try;
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Stream;

public abstract class Server implements AutoCloseable {
    protected String host;
    protected int port;
    protected final HttpClient client;

    public Server() {
        this.host = "127.0.0.1";
        this.port = 11434;
        client = TheShellApplication.context().httpClient();
    }

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
        client = TheShellApplication.context().httpClient();
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public abstract List<String> models() throws Exception;

    public abstract CompletableFuture<Try<Response>> talk(Request request, String model, String system);

    public abstract Stream<Try<Response>> stream(Request request, String model, String system);

    @Override
    public void close() throws IOException {
        if(client != null) {
            client.close();
        }
    }

    public abstract Server clone();
}
