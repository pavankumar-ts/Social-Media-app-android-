package com.example.collegeproject.Model;

public class ModelMsg {
    String receiver, sender, text, timeStamp;

    public ModelMsg() {
    }

    public ModelMsg(String receiver, String sender, String text, String timeStamp) {
        this.receiver = receiver;
        this.sender = sender;
        this.text = text;
        this.timeStamp = timeStamp;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
