package io.github.marchliu.intheshell.modules;

import jaskell.util.Try;

import java.util.Iterator;

public final class OllammaSession extends Session {
    public Try<Iterator<String>> stream(String content) {
        OllamaServer serv = (OllamaServer) server;
        return serv.stream(content, model, systemPrompt);
    }
}
