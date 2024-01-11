package io.github.marchliu.intheshell.modules;

import jaskell.util.Failure;
import jaskell.util.Try;
import javafx.concurrent.Task;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.net.http.HttpRequest;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

sealed public abstract class Session implements Closeable permits OllammaSession  {
    private final static Map<String, Session> sessions = new HashMap<>();

    protected String type = "ollama";
    protected Server server;
    protected String host = "127.0.0.1";
    protected int port = 11434;
    protected String model;
    protected Template template;
    protected byte[] context;
    protected String systemPrompt;
    protected List<Message> timeline = new ArrayList<>();
    protected String sessionId = UUID.randomUUID().toString();

    public String getType() {
        return type;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUrl() {
        return "http://%s:%d".formatted(host, port);
    }

    public String getModel() {
        return model;
    }

    public byte[] getContext() {
        return context;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public List<Message> getTimeline() {
        return timeline;
    }

    public String getSessionId() {
        return sessionId;
    }

    public CompletableFuture<Try<Response>> talk(Request request) {
        return server.talk(request.apply(template), this.model, this.systemPrompt);
    }

    public static Session ollama(String sessionId, String host, int port, String model, String systemPrompt, String template) {
        if (sessions.containsKey(sessionId)) {
            return sessions.get(sessionId);
        } else {
            Session session = new OllammaSession();
            session.sessionId = sessionId;
            session.host = host;
            session.port = port;
            session.server = new OllamaServer(host, port);
            session.systemPrompt = systemPrompt;
            session.model = model;
            session.template = Templates.service().getTemplate(template);
            sessions.put(sessionId, session);
            return session;
        }
    }

    public Template getTemplate() {
        return template;
    }

    public static Session ollama(String sessionId, String host, int port, String model, String template) {
        return ollama(sessionId, host, port, model,
                "You are a helpful assistant. 你是一个乐于助人的助手。",
                template);
    }

    public static Session ollama(String sessionId, String model, String template) {
        return ollama(sessionId, "127.0.0.1", 11434, model,
                "You are a helpful assistant. 你是一个乐于助人的助手。",
                template);
    }

    public static void close(String sessionId) {
        if(sessions.containsKey(sessionId)) {
            try {
                sessions.remove(sessionId).close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void close() throws IOException {
        server.close();
    }
}
