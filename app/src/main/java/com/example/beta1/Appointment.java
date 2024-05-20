package com.example.beta1;

public class Appointment {
    private String time, date,name, Cuid,Muid,requests, Cphone;

    public Appointment(String time, String date, String name, String cuid, String muid, String requests,String Cphone) {
        this.time = time;
        this.date = date;
        this.name = name;
        this.Cuid = cuid;
        this.Muid = muid;
        this.requests = requests;
        this.Cphone  = Cphone;

    }

    public String getCphone() {
        return Cphone;
    }

    public void setCphone(String cphone) {
        Cphone = cphone;
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
