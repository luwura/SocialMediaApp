package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.domain.Friendship;
import org.example.domain.Tuple;
import org.example.service.FriendshipService;
import org.example.service.UserService;
import org.example.validation.ValidationException;

import java.time.LocalDateTime;

public class EditFriendshipController {
    @FXML
    private TextField textFieldUser1;  // Assuming this should be textFieldFriendshipUser1Id
    @FXML
    private TextField textFieldUser2;  // Assuming this should be textFieldFriendshipUser2Id
    @FXML
    private TextField textFieldDate;     // Assuming this should be textFieldFriendshipDate
    private FriendshipService service;
    private UserService userService;
    Stage dialogStage;
    @FXML
    private void initialize() {
    }

    public void setService(FriendshipService service, UserService userService, Stage stage) {
        this.service = service;
        this.userService = userService;
        this.dialogStage = stage;
        //setFields(s);  // Assuming you want to populate fields when the service is set
    }

    @FXML
    public void handleSave() {
        String[] user1Id = textFieldUser1.getText().split("\\s+");
        String[] user2Id = textFieldUser2.getText().split("\\s+");
        Friendship f = service.add(new Friendship(new Tuple<>(userService.find(Long.parseLong(user1Id[0])),userService.find(Long.parseLong(user2Id[0])))));
        saveFriendship(f);
    }


    private void saveFriendship(Friendship friendship) {
        // TODO
        try {
            Friendship f = this.service.add(friendship);
            if (f == null)
                dialogStage.close();
            UserAlert.showMessage(null, Alert.AlertType.INFORMATION, "Save Friendship", "Friendship has been saved");
        } catch (ValidationException e) {
            UserAlert.showErrorMessage(null, e.getMessage());
        }
        dialogStage.close();
    }

    private void clearFields() {
        textFieldUser1.setText("");
        textFieldUser2.setText("");
        textFieldDate.setText("");
    }

    private void setFields(Friendship f) {
        textFieldUser1.setText(f.getFriendsPair().first().getId()+" "+f.getFriendsPair().first().getFirstName()+" "+f.getFriendsPair().first().getLastName());
        textFieldUser2.setText(f.getFriendsPair().first().getId()+" "+f.getFriendsPair().second().getFirstName()+" "+f.getFriendsPair().first().getLastName());
        textFieldDate.setText(f.getDate().toString());
    }

    @FXML
    public void handleCancel() {
        dialogStage.close();
    }
}
