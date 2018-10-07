package ru.ssnd.demo.vkchat.entity;

import com.google.gson.annotations.Expose;

import javax.persistence.Entity;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class Message {

    //TODO Define all essential fields
    // https://vgy.me/5AoR4Y.png - after gson

    @Expose
    private Long id;
    @Expose
    private Boolean sent;
    @Expose
    private Sender sender;
    @Expose
    private Date sentAt;
    @Expose
    private String text;

    public Message() {
        this.id = 0L;
        this.sent = false;
        this.sender = null;
        this.sentAt = new Date();
        this.text = "";
    }

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

    public Boolean getSent() {
        return sent;
    }

    public void setSent(Boolean sent) {
        this.sent = sent;
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
                + "\nSender ID: " + (sender != null ? sender.getId() : "Anonymous")
                + "\nSent at: " + formattedDate
                + "\nText: " + text;
    }
}
