package org.example;

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
import org.example.controller.EditFriendshipController;
import org.example.controller.UserAlert;
import org.example.domain.Friendship;
import org.example.service.FriendshipService;
import org.example.service.UserService;
import org.example.utils.events.FriendshipChangeEvent;
import org.example.utils.events.UserChangeEvent;
import org.example.utils.observer.Observer;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendshipAdminController  implements Observer<FriendshipChangeEvent> {
    FriendshipService service;
    UserService userService;
    ObservableList<Friendship> model = FXCollections.observableArrayList();


    @FXML
    TableView<Friendship> tableView;
    @FXML
    TableColumn<Friendship,Long> tableColumnUser1;
    @FXML
    TableColumn<Friendship,Long> tableColumnUser2;
    @FXML
    TableColumn<Friendship,String> tableColumnDate;

    public void setService(FriendshipService service, UserService userService) {
        this.userService = userService;
        this.service = service;
        this.service.addObserver(this);
        initModel();
    }

    @FXML
    public void initialize() {
        tableColumnUser1.setCellValueFactory(new PropertyValueFactory<Friendship, Long>("firstUserId"));
        tableColumnUser2.setCellValueFactory(new PropertyValueFactory<Friendship, Long>("secondUserId"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<Friendship, String>("Date"));
        tableView.setItems(model);
    }

    private void initModel() {
        Iterable<Friendship> friendships = service.getAll();
        List<Friendship> friendshipsList = StreamSupport.stream(friendships.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(friendshipsList);
    }

    public void handleDeleteFriendship(ActionEvent actionEvent) {
        Friendship selected = (Friendship) tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Friendship deleted = service.delete(selected);
            if (null != deleted)
                UserAlert.showMessage(null, Alert.AlertType.INFORMATION, "Delete", "Friendship a fost sters cu succes!");
        } else UserAlert.showErrorMessage(null, "Nu ati selectat nici un friendship!");
    }

    @Override
    public void update(FriendshipChangeEvent userChangeEvent) {

        initModel();
    }

    @FXML
    public void handleAddFriendship(ActionEvent ev) {
        // TODO
        showFriendshipEditDialog();
    }

    public void showFriendshipEditDialog() {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("views/friendshipEdit-view.fxml"));


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Friendship");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            EditFriendshipController editMessageViewController = loader.getController();
            editMessageViewController.setService(service, userService,dialogStage);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
