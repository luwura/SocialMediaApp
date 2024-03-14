package org.example.domain;

import java.time.LocalDateTime;


public class Friendship extends Entity<Tuple<Long,Long>>{
    private Tuple<User,User> friendsPair;
    private Long firstUserId;

    public Long getFirstUserId() {
        return firstUserId;
    }

    public Long getSecondUserId() {
        return secondUserId;
    }

    private Long secondUserId;
    private LocalDateTime date;
    public Friendship(Tuple<User,User> friendsPair,LocalDateTime date){
        this.friendsPair=friendsPair;
        this.setId(new Tuple<>(friendsPair.first().getId(),friendsPair.second().getId()));
        firstUserId = friendsPair.first().getId();
        secondUserId = friendsPair.second().getId();
        this.date=date;
    }
    public Friendship(Tuple<User,User> friendsPair){
        this.friendsPair=friendsPair;
        this.date=LocalDateTime.now();
    }
    public Tuple<User,User> getFriendsPair() {
        return friendsPair;
    }

    public void setFriendsPair(Tuple<User,User> friendsPair) {
        this.friendsPair = friendsPair;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "friendsPair=" + friendsPair +
                ", date=" + date +
                '}';
    }
}

