package io.github.marchliu.intheshell.modules.generate;

import java.util.List;

final public class Request extends Message {

    public Request(String content, List<Integer> context) {
        super(content, context);
    }

    public Request apply(Template template) {
        return new Request(template.message(content), context);
    }


}
