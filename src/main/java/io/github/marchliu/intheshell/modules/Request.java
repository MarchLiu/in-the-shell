package io.github.marchliu.intheshell.modules;

import java.util.List;
import java.util.Optional;

final public class Request extends Message {
    private Optional<Long> id;

    public Request(String content, List<Integer> context) {
        super(content, context);
    }

    public Request apply(Template template) {
        return new Request(template.message(content), context);
    }

    public Optional<Long> getId() {
        return id;
    }

    public Request bindId(long id) {
        var request = new Request(content, context);
        request.id = Optional.of(id);
        return request;
    }
}
