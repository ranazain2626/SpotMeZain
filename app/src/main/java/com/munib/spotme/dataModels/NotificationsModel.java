package com.munib.spotme.dataModels;

public class NotificationsModel {

    String user_id,notification,notification_id;

    public NotificationsModel(){
    }

    public NotificationsModel(String user_id,String notifications){
        this.user_id=user_id;
        this.notification=notifications;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }

    public String getNotification_id() {
        return notification_id;
    }

    public String getNotification() {
        return notification;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setNotification(String notifications) {
        this.notification = notifications;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
