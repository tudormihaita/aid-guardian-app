package dot.help.client.events;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class EmergencyAlert {
    public static void showMessage(Stage owner, Alert.AlertType type, String header, String text,
                                   Consumer<Void> onAccept, Consumer<Void> onCancel){
        Alert message = new Alert(type);
        message.setHeaderText(header);
        message.setContentText(text);
        message.initOwner(owner);


        ButtonType acceptButtonType = new ButtonType("Accept", ButtonBar.ButtonData.YES);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.NO);
        message.getButtonTypes().setAll(acceptButtonType, cancelButtonType);


        message.showAndWait().ifPresent(response -> {
            if (response == acceptButtonType) {
                onAccept.accept(null);
            } else if (response == cancelButtonType) {
                onCancel.accept(null);
            }
        });
    }
}
