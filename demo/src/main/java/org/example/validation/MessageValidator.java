package org.example.validation;
import org.example.domain.Message;

public class MessageValidator implements Validator<Message> {
    public MessageValidator(){};
    @Override
    public void validate(Message entity) throws ValidationException {/*
        var friends=entity.getFriendsPair();
        if(friends.first().getId().equals(friends.second().getId()))
            throw new ValidationException("Can't be friend with yourself");*/
    }
}
