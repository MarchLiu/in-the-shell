package io.github.marchliu.intheshell.modules;

import java.util.HashMap;
import java.util.Map;

public class Templates {
    private static final Templates service = new Templates();

    Map<String, String> templates = new HashMap<>();

    public Templates() {
        this.templates.put("chat", chat());
        this.templates.put("translate to chinese", "翻译为中文\"\"\"%s\"\"\"");
        this.templates.put("translate to english", "translate to english\"\"\"%s\"\"\"");

    }

    /**
     * 查找模板，如果没有找到，返回 chat 模板
     * @param name 模板名
     * @return 命名对应的模板
     */
    public Template getTemplate(String name) {
        String tmpl;
        if(templates.containsKey(name)) {
            tmpl = templates.get(name);
        } else {
            tmpl =  chat();
        }
        return new Template(tmpl);
    }

    public String chat() {
        return "%s";
    }

    public static Templates service() {
        return service;
    }
}
