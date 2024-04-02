package com.example.beta1;

import java.util.ArrayList;

public class Business {
    private String name,adress,phone, active;
    private ArrayList<Service> services;
    public Business(){}
    public Business(String name, String adress, String phone, String active, ArrayList<Service> services) {
        this.name = name;
        this.adress = adress;
        this.phone = phone;
        this.active = active;
        this.services = services;
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

    public ArrayList<Service> getServices() {
        return services;
    }

    public void setServices(ArrayList<Service> services) {
        this.services = services;
    }
}
