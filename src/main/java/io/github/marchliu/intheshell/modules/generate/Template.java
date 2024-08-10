package io.github.marchliu.intheshell.modules.generate;

public class Template {
    String template;
    boolean autoEscape;

    public Template(String template) {
        this.template = template;
        autoEscape = false;
    }

    public Template(String template, boolean autoEscape) {
        this.template = template;
        this.autoEscape = autoEscape;
    }

    public String message(String content) {
        if(autoEscape) {
            var cnt = content.replace("\"\"\"", "\\\"\\\"\\\"");
            return template.formatted(cnt);
        } else {
            return template.formatted(content);
        }
    }
}
