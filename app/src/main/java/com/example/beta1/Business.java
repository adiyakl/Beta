package com.example.beta1;

import java.util.ArrayList;

public class Business {
    private String name,adress,phone, active,uid,services;
    public Business(){}

    public Business(String name, String adress, String phone, String active, String services, String uid) {
        this.name = name;
        this.adress = adress;
        this.phone = phone;
        this.active = active;
        this.services = services;
        this.uid =uid;
    }
    public String getServices() {return services;}
    public void setServices(String services) {this.services = services;}

    public String getUid() {return uid;}
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }


}
