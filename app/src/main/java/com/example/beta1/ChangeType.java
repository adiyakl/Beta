package com.example.beta1;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ChangeType {
    public static LocalDate date1;
    public static String Sdate1;

    public ChangeType() {
    }

    public static String Sdate (LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        return date.format(formatter);
    }
    public static String Odate(String date){
        StringBuilder str = new StringBuilder();
        if(date.length()>=6) {
            str.append(date.substring(4, 6));
            str.append(".");
            str.append(date.substring(2, 4));
            str.append(".");
            str.append(date.substring(0, 2));
        }
        return str.toString();
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
        public static String FBtoString(ArrayList<String> arr){
         String str = "";
         for(int i = 0;i<arr.size();i++){
             str = str+" "+arr.get(i);
         }
    return str;
    }
    public static boolean isAfterOrToday(String sdate,LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        LocalDate parsedDate = LocalDate.parse(sdate, formatter);
        if(parsedDate.isAfter(date)|| parsedDate.isEqual(date)){
            return true;
        }
        return false;
    }
    public static LocalDate Ddate(String sdate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        LocalDate parsedDate = LocalDate.parse(sdate, formatter);
        return parsedDate;
    }
    public static LocalTime Ttime(String stime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime parsedTime = LocalTime.parse(stime, formatter);
        return parsedTime;
    }

    public static String STime (String date){

        return date.substring(0,5);

    }

}