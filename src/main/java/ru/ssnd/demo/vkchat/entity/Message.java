package ru.ssnd.demo.vkchat.entity;

import com.google.gson.annotations.Expose;

import javax.persistence.Entity;
import java.sql.Date;
import java.text.SimpleDateFormat;

@Entity
public class Message {

    //TODO Define all essential fields
    // https://vgy.me/5AoR4Y.png - after gson

    @Expose
    Long id;
    @Expose
    Boolean sent = false;
    @Expose
    Sender sender;
    @Expose
    Date sentAt;
    @Expose
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

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String formattedDate = sdf.format(sentAt);

        return "Message ID: " + id
                + "\nSender ID: " + sender.getId()
                + "\nSent at: " + formattedDate
                + "\nText: " + text;
    }
}
