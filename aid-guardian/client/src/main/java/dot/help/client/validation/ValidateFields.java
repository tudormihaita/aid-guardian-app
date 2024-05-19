package dot.help.client.validation;

import javafx.scene.control.Alert;

import java.time.LocalDate;

public class ValidateFields {

    public void validate(String string, String description)
    {
        if(string.isEmpty())
        {
            ActionAlert.showMessage(null, Alert.AlertType.ERROR,"Error", description);
        }
        else if(string.matches("\\d*"))
        {
            ActionAlert.showMessage(null, Alert.AlertType.ERROR,"Error", "The field can't contain only numbers!");
        }
    }

    public void validateDate(LocalDate date, String description)
    {
        if (date == null)
        {
            ActionAlert.showMessage(null, Alert.AlertType.ERROR,"Error", description);
        }
        if (date.isAfter(LocalDate.now()))
        {
            ActionAlert.showMessage(null, Alert.AlertType.ERROR,"Error", "Date of birth cannot be in the future");
        }
    }
}
