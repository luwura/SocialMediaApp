package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.domain.User;
import org.example.service.UserService;
import org.example.validation.ValidationException;
import org.example.validation.Validator;

public class EditUserController {
    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldLastName;
    @FXML
    private TextField textFieldUsername;
    @FXML
    private TextField textFieldPassword;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private DatePicker datePickerDate;

    private UserService service;
    private Validator<User> validator;
    Stage dialogStage;
    User user;

    @FXML
    private void initialize() {
    }

    public void setService(UserService service, Stage stage, User s, Validator<User>  validator) {
        this.service = service;
        this.dialogStage=stage;
        this.user =s;
        this.validator = validator;
    }

    @FXML
    public void handleSave(){
        String firstName= textFieldFirstName.getText();
        String lastName= textFieldLastName.getText();
        String username= textFieldUsername.getText();
        String password= textFieldPassword.getText();
        String email= textFieldEmail.getText();
        try {
            User testUser = new User(firstName,lastName,username,password,email);
            testUser.setId(1l);
            validator.validate(testUser);

        } catch (ValidationException e) {
            UserAlert.showErrorMessage(null,e.getMessage());
            return;
        }
        dialogStage.close();
        User u=new User(firstName,lastName,username,service.encrypt(password),email);
        if (null == this.user)
            saveUser(u);
        else {
            u.setId(user.getId());
            updateUser(u);
        }
    }

    private void updateUser(User user)
    {
        User u= this.service.update(user);
        if (u==user)
            UserAlert.showMessage(null, Alert.AlertType.INFORMATION,"Modificare user","User-ul a fost modificat");

        dialogStage.close();
    }


    private void saveUser(User user)
    {
        User u= this.service.add(user);
        if (u==null)
            dialogStage.close();
        UserAlert.showMessage(null, Alert.AlertType.INFORMATION,"Salvare user","User-ul a fost salvat");
        dialogStage.close();

    }

    private void clearFields() {
        textFieldFirstName.setText("");
        textFieldLastName.setText("");
        textFieldUsername.setText("");
        textFieldPassword.setText("");
        textFieldEmail.setText("");
    }
    private void setFields(User s)
    {
        textFieldFirstName.setText(s.getFirstName());
        textFieldLastName.setText(s.getLastName());
        textFieldUsername.setText(s.getUsername());
        textFieldPassword.setText(s.getPassword());
        textFieldEmail.setText(s.getEmail());
    }

    @FXML
    public void handleCancel(){
        dialogStage.close();
    }
}
