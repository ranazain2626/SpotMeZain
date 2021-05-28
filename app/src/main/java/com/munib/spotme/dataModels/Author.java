package com.munib.spotme.dataModels;

import com.stfalcon.chatkit.commons.models.IUser;

import java.io.Serializable;

public class Author implements IUser, Serializable {

    String id,name,avatar;
    /*...*/

    public Author(String id)
    {
        this.id=id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }
}