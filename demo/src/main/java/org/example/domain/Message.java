package org.example.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long>{

    private User from;
    private List<User> to;
    private String message;
    private LocalDateTime data;
    private Long replyTo;

    public Message(User from, List<User> to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.data = LocalDateTime.now();
        this.replyTo =null;
    }

    public Message(User from, List<User> to, String message,Long replyTo) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.data = LocalDateTime.now();
        this.replyTo =replyTo;
    }

    public Message(User from, List<User> to, String message, LocalDateTime data, Long replyTo) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.data = data;
        this.replyTo = replyTo;
    }

    public Message(User from, List<User> to, String message, LocalDateTime data) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.data = data;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public List<User> getTo() {
        return to;
    }

    public void setTo(List<User> to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return data;
    }

    public void setDate(LocalDateTime data) {
        this.data = data;
    }

    public Long getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(Long replyTo) {
        this.replyTo = replyTo;
    }
}
