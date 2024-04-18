package com.example.beta1;

public class Appointment {
    private String time, date;
    private String text ;

    public Appointment() {

    }

    public Appointment(String time, String date, String text) {
        this.time = time;
        this.date = date;
        this.text = text;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
