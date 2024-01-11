package io.github.marchliu.intheshell.modules;

import java.time.LocalDateTime;
import java.util.List;

sealed abstract public class Message permits Request, Response {
    protected String content;
    protected List<Integer> context;
    protected LocalDateTime timestamp = LocalDateTime.now();

    protected Message(String content, List<Integer> context) {
        this.content = content;
        this.context = context;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public List<Integer> getContext() {
        return context;
    }
}
