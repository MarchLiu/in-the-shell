package io.github.marchliu.intheshell.modules;

import java.util.List;

final public class Response extends Message {
    public Response(String content, List<Integer> context) {
        super(content, context);
    }

    public Response append(String segment) {
        return new Response(content + segment, context);
    }
}
