package ru.ssnd.demo.vkchat.entity;

import javax.persistence.Entity;

@Entity
public class Message {

    //TODO Define all essential fields
    // https://vgy.me/5AoR4Y.png - after gson
    Long id;
    Sender sender;
    Long sentAt;
    String text;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public Long getSentAt() {
        return sentAt;
    }

    public void setSentAt(Long sentAt) {
        this.sentAt = sentAt;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
