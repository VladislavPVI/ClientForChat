package com.pvi.myClient.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ChatMessage {
    private String name;
    private String text;
    private MessageType type;
    private String time;

    @JsonIgnore
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm a");

    public ChatMessage(String name, String text, MessageType type) {
        this.name = name;
        this.text = text;
        this.type = type;
        this.time = LocalTime.now().format(dtf);
    }

    public ChatMessage() {
        this.time = LocalTime.now().format(dtf);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "name=" + name +
                ", text='" + text + '\'' +
                ", type=" + type +
                ", time=" + time + '}';
    }
}
