package io.github.marchliu.intheshell;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.HashMap;
import java.util.Map;

public class AppConfiguration  {
    Config conf;
    private final Map<String, Integer> autoPort = new HashMap<>();
    private static final AppConfiguration instance = new AppConfiguration();

    private AppConfiguration() {
        conf = ConfigFactory.load();
        int bar1 = conf.getInt("foo.bar");
        var hosts = conf.getObject("hosts").unwrapped();
        for(var host: hosts.keySet()) {

        }
    }

    public static AppConfiguration getInstance() {
        return instance;
    }
}
