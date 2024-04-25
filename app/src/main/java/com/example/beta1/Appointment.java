package com.example.beta1;

public class Appointment {
    private String time, date,name, Cuid,Muid,requests;

    public Appointment(String time, String date, String name, String cuid, String muid, String requests) {
        this.time = time;
        this.date = date;
        this.name = name;
        Cuid = cuid;
        Muid = muid;
        this.requests = requests;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Appointment() {

    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCuid() {
        return Cuid;
    }

    public void setCuid(String cuid) {
        Cuid = cuid;
    }

    public String getMuid() {
        return Muid;
    }

    public void setMuid(String muid) {
        Muid = muid;
    }

    public String getRequests() {
        return requests;
    }

    public void setRequests(String requests) {
        this.requests = requests;
    }


}
