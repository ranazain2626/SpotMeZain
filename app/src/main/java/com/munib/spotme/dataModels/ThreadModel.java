package com.munib.spotme.dataModels;

public class ThreadModel {

    String chat_with, last_message,thread_id;
    int notification_count=0;

    public ThreadModel(){}
    public ThreadModel(String chat_with,String last_message,int notification_count)
    {
        this.chat_with=chat_with;
        this.last_message=last_message;
        this.notification_count=notification_count;
    }

    public int getNotification_count() {
        return notification_count;
    }

    public void setThread_id(String thread_id) {
        this.thread_id = thread_id;
    }

    public String getThread_id() {
        return thread_id;
    }

    public String getChat_with() {
        return chat_with;
    }

    public String getLast_message() {
        return last_message;
    }
}

