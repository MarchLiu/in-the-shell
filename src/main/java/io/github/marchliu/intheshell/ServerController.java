package io.github.marchliu.intheshell;

import io.github.marchliu.intheshell.modules.OllamaServer;
import io.github.marchliu.intheshell.modules.Server;
import io.github.marchliu.intheshell.modules.Session;
import io.github.marchliu.intheshell.modules.Templates;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class ServerController {
    @FXML
    private ListView<String> modelsListView;

    private Server server;

    @FXML
    private final ReadOnlyObjectProperty<ObservableList<String>> models =
            new SimpleObjectProperty<>(FXCollections.observableArrayList());

    public Server getServer() {
        return server;
    }

    public void init() throws Exception {
        loadModels();
        modelsListView.itemsProperty().bind(models);

        modelsListView.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty());
            bindContextMenu(cell);
            return cell;
        });

    }

    public void loadModels() throws Exception {
        server = new OllamaServer();
        List<String> result = server.models();
        models.get().clear();
        models.get().addAll(result);
    }

    private void bindContextMenu(ListCell<String> cell) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem chatMenu = new MenuItem("Chat");

        chatMenu.setOnAction(event -> {
            String model = cell.itemProperty().get();
            try {
                Stage stage = new Stage();
                FXMLLoader sessionLoader =
                        new FXMLLoader(TheShellApplication.class.getResource("session-view.fxml"));
                Scene scene = new Scene(sessionLoader.load());
                SessionController controller = sessionLoader.getController();
                controller.init(server.clone(), model, "chat");
                stage.setOnCloseRequest(evt -> {
                    controller.onClose();
                });
                stage.setTitle("Chat %s(%s:%d): %s".formatted(
                        "Ollama", server.getHost(), server.getPort(), model
                ));
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        MenuItem tran2CN = new MenuItem("Translate to Chinese");
        tran2CN.setOnAction(event -> {
            String model = cell.itemProperty().get();
            try {
                Stage stage = new Stage();
                FXMLLoader sessionLoader =
                        new FXMLLoader(TheShellApplication.class.getResource("session-view.fxml"));
                Scene scene = new Scene(sessionLoader.load());
                SessionController controller = sessionLoader.getController();
                controller.init(server.clone(), model, "translate to chinese");
                stage.setOnCloseRequest(evt -> {
                    controller.onClose();
                });
                stage.setTitle("翻译为中文 %s(%s:%d): %s".formatted(
                        "Ollama", server.getHost(), server.getPort(), model
                ));
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        MenuItem tran2EN = new MenuItem("Translate to English");
        tran2EN.setOnAction(event -> {
            String model = cell.itemProperty().get();
            try {
                Stage stage = new Stage();
                FXMLLoader sessionLoader =
                        new FXMLLoader(TheShellApplication.class.getResource("session-view.fxml"));
                Scene scene = new Scene(sessionLoader.load());
                SessionController controller = sessionLoader.getController();
                controller.init(server.clone(), model, "translate to chinese");
                stage.setOnCloseRequest(evt -> {
                    controller.onClose();
                });
                stage.setTitle("Translate to English %s(%s:%d): %s".formatted(
                        "Ollama", server.getHost(), server.getPort(), model
                ));
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        contextMenu.getItems().add(chatMenu);
        contextMenu.getItems().add(tran2CN);
        contextMenu.getItems().add(tran2EN);

        cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
            if (isNowEmpty) {
                cell.setContextMenu(null);
            } else {
                cell.setContextMenu(contextMenu);
            }
        });
    }

    public void close() throws IOException {
        server.close();
    }
}