package com.example.beta1;

import android.widget.Toast;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class WorkWindow {
    private String wKey, note;
    ArrayList<String> partInWindow = new ArrayList<>();

    public WorkWindow() {
    }

    public WorkWindow(String wKey, String note, ArrayList<String> partInWindow) {
        this.wKey = wKey;
        this.note = note;
        this.partInWindow =partInWindow;
    }



    public String getwKey() {
        return wKey;
    }

    public void setwKey(String wKey) {
        this.wKey = wKey;
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
