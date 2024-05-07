package dot.help.client.controller.alert;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class ActionAlert {

    public static void showMessage(Stage owner, Alert.AlertType alertType, String title, String description){
        Alert message = new Alert(alertType);
        message.initOwner(owner);
        message.setTitle(title);
        message.setContentText(description);
        message.showAndWait();
    }
}
