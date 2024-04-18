package com.example.beta1;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class CalendarUtils {
    public static LocalDate selectedDate;


    //    returns string representing the month and year.
    public static String monthYearFromDate(LocalDate data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return data.format(formatter);
    }


//    //returns the array for all the days in month
//    public static ArrayList<LocalDate> daysInMonthArray(LocalDate date) {
//        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();
//        YearMonth yearMonth = YearMonth.from(date);
//        //how many days in the month
//        int daysInMonth = yearMonth.lengthOfMonth();
////        gets the date of the first day in the month.
//        LocalDate firstOfMonth = com.example.beta1.CalendarUtils.selectedDate.withDayOfMonth(1);
//        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
////      number of first day in week (1-7)
//        for (int i = 0; i <= 42; i++) {
////            if before first in the month or after the last day of the month,
////            adds a empty square.
//            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
//                daysInMonthArray.add(null);
//            } else {
//                daysInMonthArray.add(LocalDate.of(selectedDate.getYear(),selectedDate.getMonth(),i - dayOfWeek));
//            }
//        }
//        return daysInMonthArray;
//    }

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