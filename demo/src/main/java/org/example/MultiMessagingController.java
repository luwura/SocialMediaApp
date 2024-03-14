package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.domain.Message;
import org.example.domain.User;
import org.example.service.MessageService;
import org.example.utils.events.MessageChangeEvent;
import org.example.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;

public class MultiMessagingController implements Observer<MessageChangeEvent> {
    @FXML
    public TextField messsageField;
    private Stage stage;
    private MessageService messageService;
    private User loggedUser;
    private List<User> otherUser;
    private final ObservableList<String> messages = FXCollections.observableArrayList();
    @FXML
    public ListView<String> listView;
    @FXML
    public TextField messageField;

    public void setService(MessageService service, Stage stage, User loggedUser, List<User> otherUser){
        this.messageService = service;
        messageService.addObserver(this);
        this.stage = stage;
        this.loggedUser = loggedUser;
        this.otherUser = otherUser;
        initModel();
    }


    public void initializeMessages() {/*
        this.messageService.getGroupChat(loggedUser).forEach(message->{
                messages.add(message.getFrom().getFullName()+" : "+message.getMessage());
        });
        //List<Message> messageList=StreamSupport.stream(messages.spliterator(),false).collect(Collectors.toList());
        listView.setCellFactory(param -> new ListCell<String>(){
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item*//*.getMessage()*//*);
                }
            }
        });
        this.listView.setItems(messages);*/
        this.messageService.getChat(loggedUser,otherUser).forEach(message->{
            if(message.getReplyTo()!=0) {
                String msg = messageService.find(message.getReplyTo()).getMessage();
                messages.add(message.getFrom().getFullName() + " replying: " +msg+ ": " + message.getMessage());
            }
            else
                messages.add(message.getFrom().getFullName()+" : "+message.getMessage());
        });
        //List<Message> messageList=StreamSupport.stream(messages.spliterator(),false).collect(Collectors.toList());
        listView.setCellFactory(param -> new ListCell<String>(){
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item/*.getMessage()*/);
                }
            }
        });
        this.listView.setItems(messages);
    }

    private void initModel() {
        initializeMessages();
    }

    @Override
    public void update(MessageChangeEvent messageChangeEvent) {
        //messages.clear();
        initModel();
        messageField.clear();
    }

    public void handleBack(ActionEvent actionEvent) {//TODO
    }

    public void handleDeleteFriend(ActionEvent actionEvent) {//TODO
    }

    public void handleSendText(ActionEvent actionEvent) {
        String message = messageField.getText();
        if(message == null)
            return;
        else{
            Message newMessage;
            newMessage = new Message(loggedUser,otherUser, message);
            messageService.add(newMessage);
        }
    }
}

