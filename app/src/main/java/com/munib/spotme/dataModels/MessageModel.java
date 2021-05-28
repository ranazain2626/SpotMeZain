package com.munib.spotme.dataModels;

public class MessageModel {

    String sender, receiver, text, time,thread_id;

    public MessageModel(){}
    public MessageModel(String sender,String receiver,String text,String time)
    {
        this.sender=sender;
        this.receiver=receiver;
        this.text=text;
        this.time=time;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public String getThread_id() {
        return thread_id;
    }

    public String getTime() {
        return time;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setThread_id(String thread_id) {
        this.thread_id = thread_id;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
