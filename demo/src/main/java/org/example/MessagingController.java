package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.domain.Message;
import org.example.domain.User;
import org.example.service.MessageService;
import org.example.utils.events.FriendshipRequestChangeEvent;
import org.example.utils.events.MessageChangeEvent;
import org.example.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessagingController implements Observer<MessageChangeEvent>{
    @FXML
    public TextField messsageField;
    private Stage stage;
    private MessageService messageService;
    private User loggedUser;
    private User otherUser;
    private Long selectedMessage;
    private final ObservableList<Message> messages = FXCollections.observableArrayList();
    @FXML
    public ListView<Message> listView;
    @FXML
    public TextField messageField;

    public void setService(MessageService service, Stage stage, User loggedUser, User otherUser){
        this.messageService = service;
        messageService.addObserver(this);
        this.stage = stage;
        this.loggedUser = loggedUser;
        this.otherUser = otherUser;
        initModel();
    }


//    public void initializeMessages() {
//        List<User> otherUsers = new ArrayList<>();
//        otherUsers.add(otherUser);
//        this.messageService.getChat(loggedUser,otherUsers).forEach(message->{
//            if(message.getReplyTo()!=0) {
//                String msg = messageService.find(message.getReplyTo()).getMessage();
//                messages.add(message.getFrom().getFullName() + " replying: " +msg+ ": " + message.getMessage());
//            }
//            else
//                messages.add(message.getFrom().getFullName()+" : "+message.getMessage());
//        });
//        //List<Message> messageList=StreamSupport.stream(messages.spliterator(),false).collect(Collectors.toList());
//        listView.setCellFactory(param -> new ListCell<String>(){
//            @Override
//            protected void updateItem(String item, boolean empty) {
//                super.updateItem(item, empty);
//
//                if (empty || item == null) {
//                    setText(null);
//                } else {
//                    setText(item/*.getMessage()*/);
//                }
//            }
//        });
//        this.listView.setItems(messages);
//    }
private void initializeMessages() {
    List<User> otherUsers = new ArrayList<>();
//        otherUsers.add(otherUser);
    messages.setAll(messageService.getChat(loggedUser, otherUsers));
    listView.setItems(messages);
    listView.setCellFactory(param -> new ListCell<>() {
        @Override
        protected void updateItem(Message message, boolean empty) {
            super.updateItem(message, empty);

            if (empty || message == null) {
                setGraphic(null);
                setText(null);
            } else {
                HBox bubble = createBubble(message.getMessage(), message.getFrom().equals(loggedUser));
                setGraphic(bubble);
                setText(null);
            }
        }
    });
}

    private HBox createBubble(String message, boolean isSentByUser) {
        HBox bubble = new HBox();
        bubble.setPrefWidth(Region.USE_COMPUTED_SIZE);

        StackPane stackPane = new StackPane();
        Region bubbleRegion = new Region();
        bubbleRegion.getStyleClass().add("bubble");
        stackPane.getChildren().add(bubbleRegion);

        Label label = new Label(message);
        stackPane.getChildren().add(label);

        bubble.getChildren().add(stackPane);

        if (isSentByUser) {
            bubble.getStyleClass().add("sent-bubble");
        } else {
            bubble.getStyleClass().add("received-bubble");
        }

        return bubble;
    }

    private void initModel() {
        initializeMessages();
    }

    @Override
    public void update(MessageChangeEvent messageChangeEvent) {
        messages.clear();
        initModel();
        messageField.clear();
    }

    public void handleBack(ActionEvent actionEvent) {//TODO
    }

    public void handleDeleteFriend(ActionEvent actionEvent) {//TODO
    }

    public void handleSendText(ActionEvent actionEvent) {
        String message = messageField.getText();
        List<User> otherUsers = new ArrayList<>();
        otherUsers.add(otherUser);
        if(message == null)
            return;
        else{
            Message newMessage;
            if(selectedMessage!=null)
                newMessage = new Message(loggedUser,otherUsers, message, selectedMessage);
            else
                newMessage = new Message(loggedUser,otherUsers, message);
            messageService.add(newMessage);
            selectedMessage = null;
        }
    }

    public void handleReply(ActionEvent actionEvent) {
//        String repliedMessageString = listView.getSelectionModel().getSelectedItem();
//        String[] string = repliedMessageString.split(" : ");
//        String message = string[1];
//        String sender = string[0];
//        User from,to;
//        if(sender.equals(loggedUser.getFullName()))
//        {
//            from = loggedUser;
//            to = otherUser;
//        }
//        else {
//            from = otherUser;
//            to = loggedUser;
//        }
//        List<User> otherUsers = new ArrayList<>();
//        otherUsers.add(to);
//        Message repliedMessage = new Message(from,otherUsers,message);
//        selectedMessage = messageService.findId(repliedMessage);
    }
}
