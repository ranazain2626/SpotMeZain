package com.munib.spotme.dataModels;

public class InvestmentModel {
    int total_investment,shares,share_value;
    String user_uid,business_uid;

    public InvestmentModel()
    {}
    public InvestmentModel(int total_investment,int shares,int share_value)
    {
        this.total_investment=total_investment;
        this.shares=shares;
        this.share_value=share_value;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setBusiness_uid(String business_uid) {
        this.business_uid = business_uid;
    }

    public String getBusiness_uid() {
        return business_uid;
    }

    public int getShares() {
        return shares;
    }

    public int getShare_value() {
        return share_value;
    }

    public int getTotal_investment() {
        return total_investment;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public void setShare_value(int share_value) {
        this.share_value = share_value;
    }

    public void setTotal_investment(int total_investment) {
        this.total_investment = total_investment;
    }
}
