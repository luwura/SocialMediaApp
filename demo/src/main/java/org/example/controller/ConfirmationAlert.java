package org.example.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ConfirmationAlert {
    public static ButtonType showMessage(Stage owner, Alert.AlertType type, String header, String text){
        Alert message=new Alert(type);
        message.setHeaderText(header);
        message.setContentText(text);
        message.initOwner(owner);

        message.initStyle(StageStyle.UTILITY);

        // Customize the buttons
        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");

        message.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        // Show the dialog and wait for a response
        return message.showAndWait().orElse(ButtonType.CANCEL);
    }

    public static ButtonType yes(){
        return new ButtonType("Yes");
    }

    public static ButtonType no(){
        return new ButtonType("No");
    }

    public static void showErrorMessage(Stage owner, String text){
        Alert message=new Alert(Alert.AlertType.ERROR);
        message.initOwner(owner);
        message.setTitle("Mesaj eroare");
        message.setContentText(text);
        message.showAndWait();
    }
}

