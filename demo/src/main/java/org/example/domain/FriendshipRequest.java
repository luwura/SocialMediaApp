package org.example.domain;

public class FriendshipRequest extends Entity<Tuple<Long,Long>>{
    User from;
    User to;
    String status;

    public FriendshipRequest(User from, User to, String status) {
        this.from = from;
        this.to = to;
        this.status = status;
        this.setId(new Tuple<>(from.getId(),to.getId()));
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }


    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "FriendshipRequest{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }
}
