package com.example.beta1;
public class User {
    private String name, email, phone, uid, mOrC;
    public User(){}
    public User (String name, String email, String phone, String uid, String mOrC) {
        this.name=name;
        this.email=email;
        this.phone=phone;
        this.uid=uid;
        this.mOrC=mOrC;

    }

    public String getmOrC(){return mOrC;}
    public void setmOrC(String mOrC) {this.mOrC = mOrC;}

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name=name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email=email;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone=phone;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) {
        this.uid=uid;
    }

}