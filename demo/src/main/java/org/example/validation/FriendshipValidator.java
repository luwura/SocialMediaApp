package org.example.validation;

import org.example.domain.Friendship;

public class FriendshipValidator implements Validator<Friendship> {
    public FriendshipValidator(){};
    @Override
    public void validate(Friendship entity) throws ValidationException {
        var friends=entity.getFriendsPair();
        if(friends.first().getId().equals(friends.second().getId()))
            throw new ValidationException("Can't be friend with yourself");
    }
}
