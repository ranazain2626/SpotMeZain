package com.munib.spotme.dataModels;

import com.stfalcon.chatkit.commons.models.IMessage;

import java.util.Date;

public class Message implements IMessage {

    String id,text;
    Date createdAt;
    Author author;

    public Message(String id,Author author,String text,Date date)
    {
        this.id=id;
        this.text=text;
        this.author=author;
        this.createdAt=date;
    }
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Author getUser() {
        return author;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }
}