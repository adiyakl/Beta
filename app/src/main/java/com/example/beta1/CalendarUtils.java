package com.example.beta1;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class CalendarUtils {
    public static LocalDate selectedDate;
    //    returns string representing the month and year.
    public static String monthYearFromDate(LocalDate data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return data.format(formatter);
    }

    //    Creates a list of LocalDate objects for each day in the week of the provided date.
    public static ArrayList<LocalDate> daysInWeekArray(LocalDate selectedDate) {
        ArrayList<LocalDate> days = new ArrayList<>();
        LocalDate current = sundayforDate(selectedDate);
        LocalDate endDate = current.plusWeeks(1);
        while (current.isBefore(endDate)){
            days.add(current);
            current = current.plusDays(1);
        }
        return days;
    }
    private static LocalDate sundayforDate(LocalDate current) {
        LocalDate oneWeekAgo = current.minusWeeks(1);

        while (current.isAfter(oneWeekAgo))
        {
            if(current.getDayOfWeek() == DayOfWeek.SUNDAY)
                return current;

            current = current.minusDays(1);
        }

        return null;
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