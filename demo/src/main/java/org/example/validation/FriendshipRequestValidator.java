package org.example.validation;

import org.example.domain.FriendshipRequest;

public class FriendshipRequestValidator implements Validator<FriendshipRequest> {
    @Override
    public void validate(FriendshipRequest entity) throws ValidationException {
        String message = new String();
        if(entity.getFrom().getId() == entity.getTo().getId())
           message+="Can't send requests to yourself!\n";
        else if(entity.getStatus()!="accepted"||entity.getStatus()!="pending"||entity.getStatus()!="rejected")
            message+="Invalid status!\n";
        if(message!="")
            throw new ValidationException(message);
    }
}