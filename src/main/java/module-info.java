module io.github.marchliu.intheshell {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.databind;
    requires jaskell.rocks;
    requires java.sql;
    requires java.net.http;
    requires jpro.webapi;
    requires one.jpro.platform.mdfx;

    opens io.github.marchliu.intheshell to javafx.fxml;
    exports io.github.marchliu.intheshell;
    exports io.github.marchliu.intheshell.modules;
    opens io.github.marchliu.intheshell.modules to javafx.fxml;

}