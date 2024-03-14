package org.example.ui;

import org.example.domain.Friendship;
import org.example.domain.Tuple;
import org.example.domain.User;
import org.example.service.FriendshipService;
import org.example.service.UserService;
import org.example.validation.RepoException;

import java.time.LocalDateTime;
import java.util.Scanner;

public class UI {
/*    private final UserService userService;
    private final FriendshipService friendshipService;
    private Scanner scanner = new Scanner(System.in);

    public UI(UserService userservice, FriendshipService friendshipService) {
        this.userService = userservice;
        this.friendshipService = friendshipService;
    }

    private void menu(){
        System.out.println();
        System.out.println("Menu: ");
        System.out.println("1. Add user");
        System.out.println("2. Remove user");
        System.out.println("3. Print all users");
        System.out.println("4. Add friendship");
        System.out.println("5. Remove friendship");
        System.out.println("6. Print all friendships");
        System.out.println("7. Number of communities");
        System.out.println("8. Biggest community");
        System.out.println("10. The month thing");
        System.out.println("11+. Exit");
        System.out.println();
        System.out.println("Waiting for command: ");
    }

    public void run(){
        int choice;

        while(true){
            menu();
            choice = scanner.nextInt();

            switch (choice){
                case 1 -> {
                    addUser();
                }
                case 2 ->{
                    deleteUser();
                }
                case 3 ->{
                    printAllUsers();
                }
                case 4 ->{
                    addFriendship();
                }
                case 5 ->{
                    deleteFriendship();
                }
                case 6 ->{
                    printAllFriendships();
                }
                case 7 ->{
                    printNumberCommunties();
                }
                case 8 ->{
                    printMostSociableCommunity();
                }
                case 9 ->{
                    printAllCommunities();
                }
                case 10 ->{
                    printFriendsInMonth();
                }
                default -> { return; }
            }
        }
    }

    private void printFriendsInMonth() {
        System.out.println("ID:");
        Long id = scanner.nextLong();
        scanner.nextLine();
        System.out.println("Enter a LocalDateTime (yyyy-MM-ddTHH:mm:ss): \n");
        String input = scanner.nextLine();
        LocalDateTime date = LocalDateTime.parse(input);
        System.out.println("Nume|Prenume|Data");
        friendshipService.friendsFromMonth(id,date).forEach(friend->System.out.println(friend.getFriendsPair().second().getFirstName()+"|"+friend.getFriendsPair().second().getLastName()+"|"+friend.getDate()));
    }

    private void addUser(){
        System.out.println("First name:");
        String firstName = scanner.next();
        System.out.println("Last name:");
        String lastName = scanner.next();

        try{
            User user = new User(firstName,lastName);
            user.setId(1001L);
            userService.add(user);
            System.out.println("User added");
        }
        catch (RepoException e){
            System.out.println(e.getMessage());
        }
    }


    public void deleteUser(){
        printAllUsers();

        System.out.println("ID of the user:: ");
        Long idDelete = scanner.nextLong();
        try{
            this.userService.delete(userService.find(idDelete));
            System.out.println("User deleted");
        }
        catch(IllegalArgumentException e){
            System.out.println(e.getMessage());
        }

    }

    public void addFriendship(){
        printAllUsers();

        System.out.println("ID of the first user: ");
        Long id1 = scanner.nextLong();


        System.out.println("ID of the other user:");
        Long id2 = scanner.nextLong();
        try{
            Friendship friendship = new Friendship(new Tuple<>(userService.find(id1),userService.find(id2)),LocalDateTime.now());
            this.friendshipService.add(friendship);
            System.out.println("Friendship created");}
        catch (RepoException e){
            System.out.println(e.getMessage());

        }
    }

    public void deleteFriendship(){
        printAllFriendships();

        System.out.println("ID of the first user: ");
        Long id1 = scanner.nextLong();


        System.out.println("ID of the other user:");
        Long id2 = scanner.nextLong();
        try{
            Friendship friendship = friendshipService.find(new Tuple<>(id1,id2));
            this.friendshipService.delete(friendship);
            System.out.println("Friendship deleted");}
        catch (RepoException e){
            System.out.println(e.getMessage());
        }
    }



    public void printAllUsers(){
        System.out.println("Users:");
        userService.getAll().forEach(System.out::println);
        System.out.println("\n");
    }
    public void printAllFriendships(){
        System.out.println("Friendships:");
        friendshipService.getAll().forEach(System.out::println);
        System.out.println("\n");
    }

    public void printNumberCommunties(){
        System.out.println("Number of communities:");
        System.out.println(friendshipService.getAllCommunities().size()+"\n");
    }

    public void printAllCommunities(){
        System.out.println("Communities:");
        friendshipService.getAllCommunities().forEach(System.out::println);
        System.out.println("\n");
    }
    public void printMostSociableCommunity(){
        System.out.println("Most sociable community:");
        friendshipService.getMostSociableCommunity().forEach(System.out::println);
        System.out.println("\n");
    }*/
}

