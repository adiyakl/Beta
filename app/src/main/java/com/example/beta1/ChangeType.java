package com.example.beta1;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ChangeType {
    public static LocalDate date1;
    public static String Sdate1;

    public ChangeType() {
    }

    public static String Sdate (LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        return date.format(formatter);
    }

// Method to sort time strings in the format "HHmm"
        public static ArrayList<String> sortTimes(ArrayList<String> timeList) {
            // Define a custom Comparator for sorting time strings
            Comparator<String> timeComparator = new Comparator<String>() {
                @Override
                public int compare(String time1, String time2) {
                    // Compare time strings directly since they are in "HHmm" format
                    return time1.compareTo(time2);
                }
            };
            // Sort the timeList using the custom Comparator
            Collections.sort(timeList, timeComparator);
            return timeList;
        }

    public static String STime (LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return date.format(formatter);
    }

}