package io.github.marchliu.intheshell.modules;

import java.time.LocalDateTime;

sealed abstract public class Message permits Request, Response {
    protected String content;
    protected LocalDateTime timestamp = LocalDateTime.now();


    protected Message(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }


}
