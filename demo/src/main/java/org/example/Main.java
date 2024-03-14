package org.example;

import org.example.domain.Tuple;
import org.example.domain.User;
import org.example.domain.Friendship;
import org.example.repo.Repo;
import org.example.repo.db.FriendshipDBRepository;
import org.example.repo.db.UserDBRepository;
import org.example.service.FriendshipService;
import org.example.service.UserService;
import org.example.ui.UI;
import org.example.validation.UserValidator;
import org.example.validation.FriendshipValidator;
import org.example.validation.Validator;

public class Main {
    public static void main(String[] args) {/*
        Validator<User> validatorUser= new UserValidator();
        UserDBRepository repoUser = new UserDBRepository("jdbc:postgresql://localhost:5432/socialnetwork","postgres","infonebun",validatorUser);

        Validator<Friendship> validatorFriendship= new FriendshipValidator();
        FriendshipDBRepository repoFriendship = new FriendshipDBRepository("jdbc:postgresql://localhost:5432/socialnetwork","postgres","infonebun",validatorFriendship,repoUser);

        FriendshipService friendshipService = new FriendshipService(repoUser,repoFriendship);
        UserService userService = new UserService(repoUser,friendshipService);
        UI ui = new UI(userService,friendshipService);
        ui.run();*/
    }
}