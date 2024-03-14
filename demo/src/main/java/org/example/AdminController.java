package org.example;

import org.example.controller.EditUserController;
import org.example.controller.UserAlert;
import org.example.domain.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.service.UserService;
import org.example.utils.events.UserChangeEvent;
import org.example.utils.observer.Observer;
import org.example.validation.Validator;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AdminController implements Observer<UserChangeEvent> {
    UserService service;
    Validator<User> validator;
    ObservableList<User> model = FXCollections.observableArrayList();


    @FXML
    TableView<User> tableView;
    @FXML
    TableColumn<User,Long> tableColumnUserId;
    @FXML
    TableColumn<User,String> tableColumnFirstName;
    @FXML
    TableColumn<User,String> tableColumnLastName;
    @FXML
    TableColumn<User,String> tableColumnUsername;
    @FXML
    TableColumn<User,String> tableColumnPassword;
    @FXML
    TableColumn<User,String> tableColumnEmail;

    public void setService(UserService service,Validator<User>validator) {
        this.service = service;
        this.service.addObserver(this);
        this.validator = validator;
        initModel();
    }

    @FXML
    public void initialize() {
        tableColumnUserId.setCellValueFactory(new PropertyValueFactory<User, Long>("id"));
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        tableColumnUsername.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        tableColumnPassword.setCellValueFactory(new PropertyValueFactory<User, String>("password"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        tableView.setItems(model);
    }

    private void initModel() {
        Iterable<User> users = service.getAll();
        List<User> usersList = StreamSupport.stream(users.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(usersList);
    }

    public void handleDeleteUser(ActionEvent actionEvent) {
        User selected = (User) tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            User deleted = service.delete(selected);
            if (null != deleted)
                UserAlert.showMessage(null, Alert.AlertType.INFORMATION, "Delete", "Userul a fost sters cu succes!");
        } else UserAlert.showErrorMessage(null, "Nu ati selectat nici un user!");
    }

    @Override
    public void update(UserChangeEvent userChangeEvent) {

        initModel();
    }

    @FXML
    public void handleUpdateUser(ActionEvent ev) {
        User selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showUserEditDialog(selected);
        } else
            UserAlert.showErrorMessage(null, "NU ati selectat nici un user");
    }

    @FXML
    public void handleAddUser(ActionEvent ev) {
        // TODO
        showUserEditDialog(null);
    }

    public void showUserEditDialog(User user) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("views/userEdit-view.fxml"));


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit User");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            EditUserController editMessageViewController = loader.getController();
            editMessageViewController.setService(service, dialogStage, user,validator);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}