package com.munib.spotme.dataModels;

import java.io.Serializable;

public class CardModel implements Serializable {
    String id,brand,country,exp_month,exp_year,last4;

    public CardModel(String id, String brand, String country, String exp_month, String exp_year, String last4) {
        this.id = id;
        this.brand = brand;
        this.country = country;
        this.exp_month = exp_month;
        this.exp_year = exp_year;
        this.last4 = last4;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getExp_month() {
        return exp_month;
    }

    public void setExp_month(String exp_month) {
        this.exp_month = exp_month;
    }

    public String getExp_year() {
        return exp_year;
    }

    public void setExp_year(String exp_year) {
        this.exp_year = exp_year;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }
}