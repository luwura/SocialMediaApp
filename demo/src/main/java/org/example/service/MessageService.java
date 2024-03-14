package org.example.service;

import org.example.domain.Message;
import org.example.domain.User;
import org.example.repo.db.MessageDBRepository;
import org.example.utils.events.ChangeEventType;
import org.example.utils.events.MessageChangeEvent;
import org.example.utils.observer.Observable;
import org.example.utils.observer.Observer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class MessageService implements ServiceInterface<Long, Message>, Observable<MessageChangeEvent> {
    private final MessageDBRepository repoMessage;

    public MessageService(MessageDBRepository messageDBRepository) {
        this.repoMessage = messageDBRepository;
    }

    @Override
    public Message add(Message e) {
        Message returnedMessage=repoMessage.add(e).orElse(null);
        if(returnedMessage != null)
            notifyObservers(new MessageChangeEvent(ChangeEventType.ADD,returnedMessage));

        return returnedMessage;
    }

    @Override
    public Message update(Message e) {
        Message oldUser = repoMessage.findOne(e.getId()).orElse(null);
        if(oldUser != null){
            Message returnedMessage = repoMessage.update(e).orElse(null);
            notifyObservers(new MessageChangeEvent(ChangeEventType.UPDATE,returnedMessage));
            return returnedMessage;
        }

        return null;
    }

    @Override
    public Message delete(Message e) {
         Message deletedMessage = repoMessage.delete(e.getId()).orElse(null);
        if(deletedMessage != null){
            notifyObservers(new MessageChangeEvent(ChangeEventType.DELETE,deletedMessage));
        }
        return deletedMessage;
    }

    @Override
    public Message find(Long id) {
        return repoMessage.findOne(id).orElse(null);
    }



    @Override
    public List<Message> getAll() {
        List<Message> messageList = new ArrayList<>();
        for (Message message : repoMessage.getAll()) {
            messageList.add(message);
        }
        messageList.sort(Comparator.comparing(Message::getDate));

        return messageList;
    }

    private List<org.example.utils.observer.Observer<MessageChangeEvent>> observers=new ArrayList<>();
    @Override
    public void addObserver(org.example.utils.observer.Observer<MessageChangeEvent> e) {
        observers.add(e);

    }
    @Override
    public void removeObserver(Observer<MessageChangeEvent> e) {
        //observers.remove(e);
    }

    @Override
    public void notifyObservers(MessageChangeEvent t) {

        observers.stream().forEach(x -> x.update(t));
    }
    public List<Message> getChat(User loggedUser, List<User> otherUsers) {
        Iterable<Message> messagesIterable = repoMessage.getAll();
        List<Message> chatMessages = new ArrayList<>();

        messagesIterable.forEach(chatMessages::add);

        List<Message> filteredMessages = chatMessages.stream()
                .filter(message ->

                                (otherUsers.size() == 1 && message.getTo().stream().anyMatch(u -> u.equals(otherUsers.get(0))||u.equals(loggedUser)) ||
                                        (message.getFrom().equals(loggedUser) &&otherUsers.size() > 1 &&
                                                message.getTo().stream().anyMatch(u -> otherUsers.stream().anyMatch(o -> o.equals(u))) &&
                                                message.getTo().size() == otherUsers.size()))
                )
                .collect(Collectors.toList());

        return filteredMessages;
    }



    public Long findId(Message message){
        Iterable<Message> messagesIterable = repoMessage.getAll();
        List<Message> chatMessages = new ArrayList<>();

        messagesIterable.forEach(chatMessages::add);

        // Filter the messages based on the specified conditions
        List<Message> filteredMessages = chatMessages.stream()
                .filter(m ->
                        m.getMessage().equals(message.getMessage())&&
                        m.getFrom().equals(m.getFrom()) &&
                                m.getTo().containsAll(m.getTo()) &&
                                m.getTo().size() == new HashSet<>(m.getTo()).size()
                )
                .collect(Collectors.toList());

        return filteredMessages.get(0).getId();
    }
}
