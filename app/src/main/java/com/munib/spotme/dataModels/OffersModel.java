package com.munib.spotme.dataModels;

import java.io.Serializable;
import java.util.Calendar;

public class OffersModel implements Serializable {

    Boolean agreement_signed_lender,agreement_signed_borrower;
    String type,amount,amountAfterInterest,interestRate,duration,loan_agreement,lender,borrower,offer_uid;
    String date;
    int status;
    String request_message;
    int reported=0;
    public OffersModel()
    {

    }
    public OffersModel(String type,String amount,String amountAfterInterest,String interestRate,String duration,int status,String timeStamp,Boolean agreement_signed_lender,Boolean agreement_signed_borrower,String loan_agreement,String lender,String borrower){
        this.type=type;
        this.amount=amount;
        this.interestRate=interestRate;
        this.duration=duration;
        this.amountAfterInterest=amountAfterInterest;
        this.status=status;
        this.agreement_signed_lender=agreement_signed_lender;
        this.agreement_signed_borrower=agreement_signed_borrower;
        this.lender=lender;
        this.borrower=borrower;
        this.loan_agreement=loan_agreement;
        this.date= Calendar.getInstance().getTimeInMillis()+"";
    }

    public int getReported() {
        return reported;
    }

    public void setReported(int reported) {
        this.reported = reported;
    }

    public String getAmountAfterInterest() {
        return amountAfterInterest;
    }

    public void setAmountAfterInterest(String amountAfterInterest) {
        this.amountAfterInterest = amountAfterInterest;
    }

    public void setRequest_message(String request_message) {
        this.request_message = request_message;
    }

    public String getRequest_message() {
        return request_message;
    }

    public void setOffer_uid(String offer_uid) {
        this.offer_uid = offer_uid;
    }

    public String getOffer_uid() {
        return offer_uid;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public Boolean getAgreement_signed_borrower() {
        return agreement_signed_borrower;
    }

    public Boolean getAgreement_signed_lender() {
        return agreement_signed_lender;
    }

    public void setAgreement_signed_borrower(Boolean agreement_signed_borrower) {
        this.agreement_signed_borrower = agreement_signed_borrower;
    }

    public void setAgreement_signed_lender(Boolean agreement_signed_lender) {
        this.agreement_signed_lender = agreement_signed_lender;
    }

    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    public void setLender(String lender) {
        this.lender = lender;
    }

    public String getBorrower() {
        return borrower;
    }

    public String getLender() {
        return lender;
    }

    public String getLoan_agreement() {
        return loan_agreement;
    }

    public void setLoan_agreement(String loan_agreement) {
        this.loan_agreement = loan_agreement;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(String interestRate) {
        this.interestRate = interestRate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
