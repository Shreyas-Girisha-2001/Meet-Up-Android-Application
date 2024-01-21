package com.example.meet_up_application;

public class usergetset {
    String uid,username,email,profile,intrest1,intrest2,intrest3;

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getIntrest1() {
        return intrest1;
    }

    public void setIntrest1(String intrest1) {
        this.intrest1 = intrest1;
    }

    public String getIntrest2() {
        return intrest2;
    }

    public void setIntrest2(String intrest2) {
        this.intrest2 = intrest2;
    }

    public String getIntrest3() {
        return intrest3;
    }

    public void setIntrest3(String intrest3) {
        this.intrest3 = intrest3;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public usergetset(){
    }

    public usergetset(String email,String username,String uid,String intrest1,String intrest2,String intrest3,String profile){
        this.intrest1=intrest1;
        this.intrest2=intrest2;
        this.intrest3=intrest3;
        this.profile=profile;
        this.email=email;
        this.username=username;
        this.uid=uid;
    }
}

