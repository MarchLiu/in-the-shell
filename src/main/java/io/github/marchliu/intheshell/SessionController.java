package io.github.marchliu.intheshell;

import io.github.marchliu.intheshell.modules.Server;
import io.github.marchliu.intheshell.modules.Session;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionController {
    @FXML
    private TextArea textArea;

    @FXML
    ListView<String> listView;

    @FXML
    private Button send;

    private String sessionId;

    private Session session;

    private StringProperty actor = new SimpleStringProperty();

    @FXML
    protected void onSendButtonClick(Event event) {
        listView.getItems().add(textArea.getText());
        listView.refresh();
        var task = session.talk(textArea.getText());
        actor.bind(task.valueProperty());
        Thread.ofVirtual().start(task);
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
            mdv.getStylesheets().add("/com/sandec/mdfx/mdfx-default.css");
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
            text.textProperty().addListener(((observable, oldValue, newValue) -> {
                text.setWrappingWidth(tp.getWidth() - 64);
                cell.prefHeight(mdv.getLayoutBounds().getHeight() + 256);
            }));
            EventHandler<MouseEvent> handler = event -> {
                if(event.getClickCount() == 2){
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
            System.out.println(newValue);
            listView.getItems().add(newValue);
            listView.refresh();
            actor.unbind();
        });
    }

    public void onClose() {
        Session.close(sessionId);
    }
}