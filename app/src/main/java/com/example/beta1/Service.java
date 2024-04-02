package com.example.beta1;

public class Service {
    private String service;
    private double price;

    public Service() {
    }

    public Service(String service, double price) {
        this.service = service;
        this.price = price;
    }

    public String getService() {
        return service;
    }

    public double getPrice() {
        return price;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
