package dot.help.client.events;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessageAlert{
    private static final Logger logger= LogManager.getLogger(MessageAlert.class);


    public static void showMessage(Stage owner, Alert.AlertType type, String header, String text){
        logger.traceEntry("Entering showMessage");
        Alert message=new Alert(type);
        message.setHeaderText(header);
        message.setContentText(text);
        message.initOwner(owner);
        message.showAndWait();
        logger.traceExit();
    }

    public static void showErrorMessage(Stage owner, String text){
        logger.traceEntry("Entering showErrorMessage");
        Alert message=new Alert(Alert.AlertType.ERROR);
        message.initOwner(owner);
        message.setTitle("Error");
        message.setContentText(text);
        message.showAndWait();
        logger.traceExit();
    }
}

