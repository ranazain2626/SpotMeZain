package com.munib.spotme.dataModels;

import java.io.Serializable;

public class UserModel implements Serializable {
    String name, username,phone, email, password, socialSecurityNo, employmentId, address, dateOfBirth,uid,image_url,device_token,address2,city,state,zip,created_date;
    int blocked=0;
    String customer_id;

    public UserModel() {

    }

    public UserModel(String name, String username,String phone, String email, String password, String socialSecurityNo, String employmentId, String address, String dateOfBirth,String uid,String image_url,String device_token,String address2,String city,String state,String zip,int blocked,String created_date) {
        this.name = name;
        this.username=username;
        this.phone=phone;
        this.password=password;
        this.email=email;
        this.socialSecurityNo=socialSecurityNo;
        this.device_token=device_token;
        this.employmentId=employmentId;
        this.address=address;
        this.dateOfBirth=dateOfBirth;
        this.uid=uid;
        this.image_url=image_url;
        this.address2=address2;
        this.city=city;
        this.state=state;
        this.zip=zip;
        this.blocked=blocked;
        this.created_date=created_date;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setBlocked(int blocked) {
        this.blocked = blocked;
    }

    public String getCreated_date() {
        return created_date;
    }

    public int getBlocked() {
        return blocked;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress2() {
        return address2;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSocialSecurityNo() {
        return socialSecurityNo;
    }

    public void setSocialSecurityNo(String socialSecurityNo) {
        this.socialSecurityNo = socialSecurityNo;
    }

    public String getEmploymentId() {
        return employmentId;
    }

    public void setEmploymentId(String employmentId) {
        this.employmentId = employmentId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}