package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.controller.UserAlert;
import org.example.domain.Friendship;
import org.example.domain.FriendshipRequest;
import org.example.domain.Message;
import org.example.domain.User;
import org.example.repo.db.FriendshipRequestDBRepository;
import org.example.repo.db.MessageDBRepository;
import org.example.repo.paging.FriendshipDBPagingRepository;
import org.example.repo.paging.UserDBPagingRepository;
import org.example.service.FriendshipRequestService;
import org.example.service.FriendshipService;
import org.example.service.MessageService;
import org.example.service.UserService;
import org.example.validation.*;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    String url = "jdbc:postgresql://localhost:5432/socialnetwork";
    String username = "postgres";
    String password = "infonebun";
    Validator<User> userValidator = new UserValidator();
    Validator<Friendship> friendshipValidator = new FriendshipValidator();
    Validator<FriendshipRequest> friendshipRequestValidator = new FriendshipRequestValidator();
    UserDBPagingRepository userRepo = new UserDBPagingRepository(url, username, password,userValidator);
    FriendshipDBPagingRepository friendshipRepo = new FriendshipDBPagingRepository(url,username,password,friendshipValidator,userRepo);
    FriendshipRequestDBRepository friendshipRequestDBRepository = new FriendshipRequestDBRepository(url, username, password, friendshipRequestValidator, userRepo);
    FriendshipService friendshipService = new FriendshipService(userRepo,friendshipRepo);
    UserService userService = new UserService(userRepo,friendshipService);
    FriendshipRequestService friendshipRequestService = new FriendshipRequestService(userRepo,friendshipRepo,friendshipRequestDBRepository);
    Validator<Message> messageValidator = new MessageValidator();
    MessageDBRepository messageDBRepository = new MessageDBRepository(url,username,password,messageValidator,userRepo);
    MessageService messageService = new MessageService(messageDBRepository);
    @FXML
    private TextField email;


    @FXML
    private TextField passwd;

    public LoginController() throws SQLException, ClassNotFoundException {
    }

    public void handleSuccessfulLogin(){
        if(email.getText().equals("admin@admin.com") && passwd.getText().equals("admin.1")){
            showAdminScreen();
        }
        else if(email.getText().equals("friendships" )&& passwd.getText().equals("friendships.1")){
            showFriendshipAdminScreen();
        }
        else if(this.userService.checkCredentials(email.getText(),passwd.getText())!=null){
            showUserScreen(this.userService.checkCredentials(email.getText(),passwd.getText()));
        }
        else
            handleUnsuccessfulLogin();
    }


    private void showUserScreen(User loggedUser) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("views/logged-view.fxml"));


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("User Page");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);


            LoggedController loggedController = loader.getController();

            loggedController.setLoggedUser(loggedUser);
            loggedController.setService(userService, friendshipRequestService, friendshipService, messageService);


            dialogStage.show();

        } catch (IOException e ) {
            throw new RuntimeException(e);
        }
    }

    public void handleUnsuccessfulLogin(){
        UserAlert.showErrorMessage(null,"Could NOT log in! Try again.");
        this.email.clear();
        this.passwd.clear();
    }

    private void showAdminScreen(){
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("views/admin-view.fxml"));


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Admin Settings");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            AdminController adminController = loader.getController();
            adminController.setService(userService,userValidator);


            Scene scene = new Scene(root);
            dialogStage.setScene(scene);



            dialogStage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void showFriendshipAdminScreen(){
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("views/friendship-admin-view.fxml"));


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle(" Friendship Admin Settings");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            FriendshipAdminController adminController = loader.getController();
            adminController.setService(friendshipService,userService);


            Scene scene = new Scene(root);
            dialogStage.setScene(scene);



            dialogStage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

