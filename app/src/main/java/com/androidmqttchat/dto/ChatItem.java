package com.androidmqttchat.dto;

/**
 * Created by j-pc on 2018-05-18.
 */

public class ChatItem {
    private String id;
    private String content;

    public ChatItem(String id, String content) {
        this.id = id;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
