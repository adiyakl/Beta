package com.example.beta1;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class CalendarUtils {
    public static LocalDate selectedDate;
    public static ArrayList<String> hours = new ArrayList<>();
    public static ArrayList<String> DefaultHours = new ArrayList<>(Arrays.asList("sorry we don't work today"));

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


}