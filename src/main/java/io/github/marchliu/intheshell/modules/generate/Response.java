package io.github.marchliu.intheshell.modules.generate;

import java.util.List;

final public class Response extends Message {
    private boolean done = true;
    private boolean isStream = false;

    public Response(String content, List<Integer> context) {
        super(content, context);
        isStream = false;
    }

    public Response(String content, List<Integer> context, boolean done) {
        super(content, context);
        this.done = done;
        this.isStream = true;
    }

    public Response done(String segment) {
        return new Response(content + segment, context, true);
    }

    public Response append(String segment) {
        return new Response(content + segment, context);
    }

    public boolean isStream() {
        return isStream;
    }

    public boolean isDone() {
        return done;
    }
}
