package org.example.utils.events;

import org.example.domain.Friendship;
import org.example.domain.FriendshipRequest;

public class FriendshipRequestChangeEvent implements Event{
    private ChangeEventType type;
    private FriendshipRequest data, oldData;

    public FriendshipRequestChangeEvent(ChangeEventType type, FriendshipRequest data) {
        this.type = type;
        this.data = data;
    }
    public FriendshipRequestChangeEvent(ChangeEventType type, FriendshipRequest data, FriendshipRequest oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public FriendshipRequest getData() {
        return data;
    }

    public FriendshipRequest getOldData() {
        return oldData;
    }

}
