package ru.ssnd.demo.vkchat.entity;

import com.google.gson.annotations.Expose;

public class Sender {
    @Expose
    Long id;
    @Expose
    String avatarUrl;
    @Expose
    String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
