package io.github.marchliu.intheshell;

import io.github.marchliu.intheshell.modules.generate.Context;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TheShellApplication extends Application {
    public static TheShellApplication instance;

    private final Context context = new Context();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TheShellApplication.class.getResource("models-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        ServerController controller = fxmlLoader.getController();
        stage.setScene(scene);
        stage.setOnShown(windowEvent -> {
            try {
                controller.init();
                stage.setTitle("Ollama Server %s:%d".formatted(
                        controller.getServer().getHost(),
                        controller.getServer().getPort()));
            } catch (Exception err) {
                err.printStackTrace();
            }
        });
        stage.setOnCloseRequest(event -> {
            try {
                controller.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        stage.show();
    }

    @Override
    public void init() throws Exception {
        super.init();
        instance = this;
    }

    public Context getContext() {
        return context;
    }

    public static void main(String[] args) {
        launch();
    }

    public static TheShellApplication application() {
        return instance;
    }

    public static Context context() {
        return instance.context;
    }
}