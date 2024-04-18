package com.example.beta1;

import android.widget.Toast;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class WorkWindow {
    private String date, note;
    ArrayList<String> partInWindow = new ArrayList<>();

    public WorkWindow() {
    }

    public WorkWindow(String date, String note, ArrayList<String> partInWindow) {
        this.date = date;
        this.note = note;
        this.partInWindow =partInWindow;
    }



    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ArrayList<String> getPartInWindow() {
        return partInWindow;
    }

    public void setPartInWindow(ArrayList<String> partInWindow) {
        this.partInWindow = partInWindow;
    }

}
