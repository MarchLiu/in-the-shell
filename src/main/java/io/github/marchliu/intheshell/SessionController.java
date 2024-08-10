package io.github.marchliu.intheshell;

import io.github.marchliu.intheshell.modules.Server;
import io.github.marchliu.intheshell.modules.generate.*;
import jaskell.util.Failure;
import jaskell.util.Success;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.Stage;
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
    ObservableList<String> contextList = FXCollections.observableArrayList();

    @FXML
    Button sendButton;

    @FXML
    CheckBox withContext;

    @FXML
    CheckBox keepContent;

    private Session session;

    private final ObjectProperty<Response> actor = new SimpleObjectProperty<>();

    private final AtomicReference<Request> latest = new AtomicReference<>();

    private final ObjectProperty<Thread> task = new SimpleObjectProperty<>();

    @FXML
    protected void onSendButtonClick(Event event) {
        contextList.add(editor.getText());
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
        sendButton.setText("Waiting...");
        if (!keepContent.isSelected()) {
            editor.textProperty().set("");
        }

        var t = Thread.ofVirtual().start(() -> {
            session.stream(request)
                    .forEach(entry -> {
                        switch (entry) {
                            case Success(var resp):
                                Platform.runLater(() -> {
                                    actor.set(resp);
                                });
                                break;
                            case Failure(var err):
                                err.printStackTrace();
                        }
                    });
        });
        task.set(t);
    }

    public void init(Server server, String model, String templateName, Stage stage) {

        session = Session.ollama(UUID.randomUUID().toString(), server.getHost(), server.getPort(),
                model, templateName);
        listView.setItems(contextList);
        listView.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>();
            cell.setOpaqueInsets(new Insets(5, 5, 5, 5));
            cell.setStyle("""
                            -fx-border-color:darkgray;-fx-border-radius: 5px;
                            -fx-background-radius: 5;
                    """);
            MarkdownView mdv = new MarkdownView();
            mdv.fillWidthProperty().set(true);
            mdv.maxWidthProperty().bind(cell.widthProperty().subtract(3));
            mdv.getStylesheets().add("/one/jpro/platform/mdfx/mdfx-default.css");
            mdv.getStylesheets().add("/one/jpro/platform/mdfx/mdfx.css");
            mdv.mdStringProperty().bind(cell.itemProperty());
            mdv.maxWidthProperty().bind(cell.widthProperty().subtract(10));

            cell.graphicProperty().setValue(mdv);
            EventHandler<MouseEvent> handler = event -> {
                if (event.getClickCount() == 2) {
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    Map<DataFormat, Object> content = new HashMap<>();
                    content.put(DataFormat.PLAIN_TEXT, cell.itemProperty().get());
                    clipboard.setContent(content);
                }
            };
            mdv.setOnMouseClicked(handler);
            return cell;
        });

        actor.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.isStream() && oldValue != null && oldValue.isStream() && !oldValue.isDone()) {
                    int lastIndex = contextList.size() - 1;
                    String content = contextList.getLast() + newValue.getContent();
                    contextList.set(lastIndex, content);
                    if (newValue.isDone()) {
                        writeDatabase(latest.get(), new Response(content, newValue.getContext(), true));
                        resetScene();
                    }
                } else {
                    listView.getItems().add(newValue.getContent());
                    resetScene();
                }
            }
        });

        stage.setOnCloseRequest(event -> {
            if (task.get() != null && task.get().isAlive()) {
                task.get().interrupt();
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

    private void resetScene() {
        Platform.runLater(() -> {
            sendButton.setText("Send");
            sendButton.disableProperty().set(false);
            editor.setDisable(false);
            editor.setEditable(true);
            listView.refresh();
            listView.scrollTo(listView.getItems().size() - 1);
            if (task.get() != null) {
                task.set(null);
            }
        });
    }

    @FXML
    protected void onEditorKeyPress(KeyEvent event) {
        int currentPosition = editor.getCaretPosition();
        if (event.getCode() == KeyCode.E && event.isControlDown()) {
            int nextLineIndex = editor.getText().indexOf('\n', currentPosition);
            int endOfLine = (nextLineIndex == -1) ? editor.getText().length() : nextLineIndex;
            editor.positionCaret(endOfLine);
        } else if (event.getCode() == KeyCode.A && event.isControlDown()) {
            int startOfLine = editor.getText(0, currentPosition).lastIndexOf('\n');
            editor.positionCaret(startOfLine + 1);

        } else if (event.getCode() == KeyCode.K && event.isControlDown()) {
            // Ctrl-K shortcut implementation goes here
            int nextLineIndex = editor.getText().indexOf('\n', currentPosition);
            int endOfLine = (nextLineIndex == -1) ? editor.getText().length() : nextLineIndex;
            if(currentPosition == endOfLine){
                if(currentPosition < editor.getText().length()) {
                    editor.replaceText(currentPosition, currentPosition+1, "");
                }
            } else {
                editor.replaceText(currentPosition, endOfLine, "");
            }
        }
    }
}