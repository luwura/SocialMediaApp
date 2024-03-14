package org.example;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.controller.ConfirmationAlert;
import org.example.controller.UserAlert;
import org.example.domain.Friendship;
import org.example.domain.FriendshipRequest;
import org.example.domain.Tuple;
import org.example.domain.User;
import org.example.repo.pagingUtils.Page;
import org.example.repo.pagingUtils.PageableImplementation;
import org.example.service.FriendshipRequestService;
import org.example.service.FriendshipService;
import org.example.service.MessageService;
import org.example.service.UserService;
import org.example.utils.events.FriendshipRequestChangeEvent;
import org.example.utils.observer.Observer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class LoggedController implements Observer<FriendshipRequestChangeEvent>{

    public TextField pageTextField;
    Page<User> page = null;
    int initialNumberPage = 1;
    int initialSizePage = 3;

    PageableImplementation initialPageable = new PageableImplementation(initialNumberPage,initialSizePage);

    UserService service;
    FriendshipRequestService friendshipRequestService;
    FriendshipService friendshipService;
    MessageService messageService;
    ObservableList<User> modelFriends = FXCollections.observableArrayList();
    ObservableList<User> modelPending = FXCollections.observableArrayList();
    ObservableList<User> modelOthers = FXCollections.observableArrayList();
    User loggedUser;

    @FXML
    AnchorPane chatPane;

    @FXML
    AnchorPane reqPane;

    @FXML
    ChoiceBox<String> choiceBox;

    @FXML
    TableView<User> friendsTableView;
    @FXML
    TableColumn<User,Long> tableColumnFriendsId;
    @FXML
    TableColumn<User,String> tableColumnFriendsName;

    @FXML
    TableView<User> pendingTableView;
    @FXML
    TableColumn<User,Long> tableColumnPendingId;
    @FXML
    TableColumn<User,String> tableColumnPendingName;

    @FXML
    TableView<User> othersTableView;
    @FXML
    TableColumn<User,Long> tableColumnOthersId;
    @FXML
    TableColumn<User,String> tableColumnOthersName;

    public void setService(UserService service, FriendshipRequestService friendshipRequestService, FriendshipService friendshipService,MessageService messageService) {
        this.service = service;
        this.friendshipRequestService = friendshipRequestService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.friendshipRequestService.addObserver(this);
        initModel();
        setEvents();
    }

    private void setEvents() {
        //friendsTableView.setOnMouseClicked(event -> handleOthersTableViewClick());
        pendingTableView.setOnMouseClicked(event -> handleRespondToRequest(null));
        othersTableView.setOnMouseClicked(event -> handleSendRequest(null));

    }


    public void setLoggedUser(User user) {
        this.loggedUser = user;
    }

    public void setPage(){
        pageTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!pageTextField.getText().isEmpty()){
                    initialSizePage = Integer.parseInt(pageTextField.getText());
                    initModel();
                }

            }
        });
        pageTextField.setText("3");
    }

    @FXML
    public void initialize() {
        tableColumnFriendsId.setCellValueFactory(new PropertyValueFactory<User, Long>("id"));
        tableColumnFriendsName.setCellValueFactory(new PropertyValueFactory<User, String>("fullName"));
        friendsTableView.setItems(modelFriends);

        tableColumnPendingId.setCellValueFactory(new PropertyValueFactory<User, Long>("id"));
        tableColumnPendingName.setCellValueFactory(new PropertyValueFactory<User, String>("fullName"));
        pendingTableView.setItems(modelPending);

        tableColumnOthersId.setCellValueFactory(new PropertyValueFactory<User, Long>("id"));
        tableColumnOthersName.setCellValueFactory(new PropertyValueFactory<User, String>("fullName"));
        othersTableView.setItems(modelOthers);

        reqPane.setVisible(false);
    }

    private void initModel() {
        Iterable<User> friends = friendshipRequestService.getUsersByStatus(loggedUser,"accepted");
        List<User> friendsList = StreamSupport.stream(friends.spliterator(), false)
                .collect(Collectors.toList());
        modelFriends.setAll(friendsList);

        Iterable<User> pending = friendshipRequestService.getUsersByStatus(loggedUser,"pending");
        List<User> pendingList = StreamSupport.stream(pending.spliterator(), false)
                .collect(Collectors.toList());
        modelPending.setAll(pendingList);
        friendsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Iterable<User> others = friendshipRequestService.getUsersByStatus(loggedUser,"rejected");
        List<User> othersList = StreamSupport.stream(others.spliterator(), false)
                .collect(Collectors.toList());
        modelOthers.setAll(othersList);
    }

    public void handleSwitchScenes(ActionEvent actionEvent){
        String selectedOption = choiceBox.getValue();

        // Check the selected option and call the appropriate method
        switch (selectedOption) {
            case "Chats tab":
                handleToChatsScene();
                break;
            case "Requests tab":
                handleToRequestsScene();
                break;
            default:
                break;
        }
    }

    private void handleToRequestsScene() {
        chatPane.setVisible(false);
        reqPane.setVisible(true);
    }

    public void handleToChatsScene(){
        chatPane.setVisible(true);
        reqPane.setVisible(false);
    }


    private void handleRespondToRequest(Object o) {
        ButtonType result = ConfirmationAlert.showMessage(
                null,
                Alert.AlertType.CONFIRMATION,
                "Hold on",
                "Accept the friendship request?"
        );

        if (result.getText() == "Yes") {
            User otherUser = pendingTableView.getSelectionModel().getSelectedItem();
            FriendshipRequest friendshipRequest = new FriendshipRequest(otherUser,loggedUser,"accepted");
            friendshipRequestService.update(friendshipRequest);
            Friendship friendship = new Friendship(new Tuple<>(loggedUser,otherUser));
            friendshipService.add(friendship);
        }
        else if (result.getText() == "No") {
            FriendshipRequest friendshipRequest = new FriendshipRequest(loggedUser, othersTableView.getSelectionModel().getSelectedItem(),"rejected");
            friendshipRequestService.update(friendshipRequest);
        }
    }


    @Override
    public void update(FriendshipRequestChangeEvent userChangeEvent) {
        initModel();
    }

    @FXML
    public void handleSendRequest(ActionEvent ev) {
        ButtonType result = ConfirmationAlert.showMessage(
                null,
                Alert.AlertType.CONFIRMATION,
                "Hold on",
                "Send a friendship request?"
        );

        if (result.getText() == "Yes") {
            FriendshipRequest friendshipRequest = new FriendshipRequest(loggedUser, othersTableView.getSelectionModel().getSelectedItem(),"pending");
            friendshipRequestService.add(friendshipRequest);
        }
    }

    public void handleStartChat(ActionEvent ev) {
        ObservableList<User> selectedUsers = friendsTableView.getSelectionModel().getSelectedItems();

        if (selectedUsers.isEmpty()) {
            UserAlert.showMessage(null, Alert.AlertType.WARNING, "Selection Empty", "Please select at least 2 users!");
            return;
        }

        if (selectedUsers.size() == 1) {
            showDirectMessagingScreen(selectedUsers.get(0));
        } else {
            List<User> friendsToSend = new ArrayList<>(selectedUsers);
            showMultiMessagingScreen(friendsToSend);
        }
    }

    private void showMultiMessagingScreen(List<User> friendsToSend) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("views/multi-messaging-view.fxml"));


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Chat");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            MultiMessagingController messagingController = loader.getController();
            messagingController.setService(messageService, dialogStage, loggedUser, friendsToSend);
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showDirectMessagingScreen(User friend) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("views/direct-messaging-view.fxml"));


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Chat");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            MessagingController messagingController = loader.getController();
            messagingController.setService(messageService, dialogStage, loggedUser, friend);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleLastPage(ActionEvent actionEvent) {
    }

    public void handleNextPage(ActionEvent actionEvent) {
    }
}
