package io.github.marchliu.intheshell;

import io.github.marchliu.intheshell.modules.*;
import jaskell.util.Failure;
import jaskell.util.Success;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import one.jpro.platform.mdfx.MarkdownView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class SessionController {
    @FXML
    private TextArea editor;

    @FXML
    ListView<String> listView;

    @FXML
    Button sendButton;

    @FXML
    CheckBox withContext;

    private Session session;

    private final ObjectProperty<Response> actor = new SimpleObjectProperty<>();

    private final AtomicReference<Request> latest = new AtomicReference<>();

    @FXML
    protected void onSendButtonClick(Event event) {
        listView.getItems().add(editor.getText());
        listView.refresh();
        Request request;
        Message message = actor.get();
        if (message != null && withContext.isSelected()) {
            request = new Request(editor.getText(), message.getContext());
        } else {
            request = new Request(editor.getText(), new ArrayList<>());
        }
        latest.set(request);
        editor.setEditable(false);
        sendButton.setDisable(true);

        Thread.ofVirtual().start(() -> {
            session.talk(request).thenAcceptAsync(result -> {
                switch (result) {
                    case Success(var response):
                        Platform.runLater(() -> actor.set(response));
                        break;
                    case Failure(var err):
                        err.printStackTrace();
                }
                Platform.runLater(()->{
                    editor.setEditable(true);
                    sendButton.setDisable(false);
                });
            });
        });
    }

    public void init(Server server, String model, String templateName) {

        session = Session.ollama(UUID.randomUUID().toString(), server.getHost(), server.getPort(),
                model, templateName);

        listView.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>();
            TabPane tp = new TabPane();
            tp.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
            tp.paddingProperty().set(new Insets(0, 10, 10, 10));
            MarkdownView mdv = new MarkdownView();
            mdv.getStylesheets().add("/one/jpro/platform/mdfx/mdfx-default.css");
            mdv.getStylesheets().add("/one/jpro/platform/mdfx/mdfx.css");
            Tab mdTab = new Tab("markdown", mdv);

            mdv.mdStringProperty().bind(cell.itemProperty());
            tp.getTabs().add(mdTab);
            Text text = new Text();
            Tab ptTab = new Tab("plain", text);
            tp.getTabs().add(ptTab);
            tp.setSide(Side.BOTTOM);
            cell.graphicProperty().setValue(tp);
            text.textProperty().bind(cell.itemProperty());
            tp.visibleProperty().bind(cell.itemProperty().isNotNull());
            cell.itemProperty().addListener(((observable, oldValue, newValue) -> {
                text.setWrappingWidth(cell.getScene().getWidth() - 64);
                mdv.setPrefWidth(cell.getScene().getWidth() - 64);
                cell.prefHeight(mdv.getLayoutBounds().getHeight() + 32);
            }));
            EventHandler<MouseEvent> handler = event -> {
                if (event.getClickCount() == 2) {
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    Map<DataFormat, Object> content = new HashMap<>();
                    content.put(DataFormat.PLAIN_TEXT, cell.itemProperty().get());
                    clipboard.setContent(content);
                }
            };
            mdv.setOnMouseClicked(handler);
            text.setOnMouseClicked(handler);
            return cell;
        });

        actor.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                listView.getItems().add(newValue.getContent());
                writeDatabase(latest.get(), newValue);
                listView.refresh();
                listView.scrollTo(listView.getItems().size() - 1);
            }
        });

    }

    public void writeDatabase(Request request, Response response) {
        System.out.printf("with context length %d \n", request.getContext().size());
        System.out.printf("- %s \n- %s\n", request.getContent(), response.getContent());
    }

    public void onClose() {
        Session.close(session.getSessionId());
    }
}