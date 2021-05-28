package com.munib.spotme.dataModels;

import com.munib.spotme.BusinessProfileActivity;

import java.io.Serializable;

public class BusinessProfileModel implements Serializable {
    String business_name,tax_id,ein_number,first_name,last_name,social_security_no,description,image_url,uid,phone,email;
    int status,share_value,shares;
    public BusinessProfileModel()
    {

    }

    public BusinessProfileModel( String business_name,String tax_id,String ein_number,String first_name,String last_name,String social_security_no,String description,String image_url,int status,int shares,int share_value,String phone,String email)
    {
        this.business_name=business_name;
        this.tax_id=tax_id;
        this.ein_number=ein_number;
        this.first_name=first_name;
        this.last_name=last_name;
        this.social_security_no=social_security_no;
        this.description=description;
        this.image_url=image_url;
        this.status=status;
        this.share_value=share_value;
        this.shares=shares;
        this.phone=phone;
        this.email=email;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getShare_value() {
        return share_value;
    }

    public int getShares() {
        return shares;
    }

    public void setShare_value(int share_value) {
        this.share_value = share_value;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }

    public String getTax_id() {
        return tax_id;
    }

    public void setTax_id(String tax_id) {
        this.tax_id = tax_id;
    }

    public String getEin_number() {
        return ein_number;
    }

    public void setEin_number(String ein_number) {
        this.ein_number = ein_number;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getSocial_security_no() {
        return social_security_no;
    }

    public void setSocial_security_no(String social_security_no) {
        this.social_security_no = social_security_no;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
