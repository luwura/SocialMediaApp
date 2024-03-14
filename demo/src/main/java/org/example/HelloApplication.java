package org.example;

import org.example.domain.Friendship;
import org.example.domain.User;
import org.example.repo.paging.FriendshipDBPagingRepository;
import org.example.repo.paging.UserDBPagingRepository;
import org.example.service.FriendshipService;
import org.example.service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.validation.FriendshipValidator;
import org.example.validation.UserValidator;
import org.example.validation.Validator;

import java.io.IOException;
import java.sql.SQLException;

public class HelloApplication extends Application {

    private UserService userService;

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("views/loginScreen-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 634, 450);
        stage.setTitle("Social Network App");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        launch();
    }
}
