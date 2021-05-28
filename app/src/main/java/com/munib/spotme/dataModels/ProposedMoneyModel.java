package com.munib.spotme.dataModels;

import com.munib.spotme.adapters.ProposedMoneyAdapter;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProposedMoneyModel implements Serializable {

    double amount;
    int index,status,extension_requested=0;
    String due_date,month,uid;

    public ProposedMoneyModel(){}
    public ProposedMoneyModel(int index,String month,double amount,String due_date,int status,int extension_requested)
    {
        this.status=status;
        this.index=index;
        this.month=month;
        this.amount=amount;
        this.due_date=due_date;
        this.extension_requested=extension_requested;
    }

    public int getExtension_requested() {
        return extension_requested;
    }

    public void setExtension_requested(int extension_requested) {
        this.extension_requested = extension_requested;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public int getStatus() {
        return status;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDue_date() {
        return due_date;
    }

    public double getAmount() {
        return amount;
    }

    public int getIndex() {
        return index;
    }


    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
