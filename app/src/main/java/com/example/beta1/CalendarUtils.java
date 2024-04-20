package com.example.beta1;

import static com.example.beta1.ChangeType.Sdate;
import static com.example.beta1.DBref.mAuth;
import static com.example.beta1.DBref.refActiveCalendar;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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

//    public static void setHours(ArrayList<String> DefaultHours) {
//        CalendarUtils.DefaultHours = DefaultHours;
//    }

//    public static ArrayList<String> getDefaultHours() {
//        String uid = "0";
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            uid = currentUser.getUid();
//        }
//        refActiveCalendar.child(uid).child("DefaultWindow").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if(task.isSuccessful()){
//                    DataSnapshot ds = task.getResult();
//                    WorkWindow DBWindow = ds.getValue(WorkWindow.class);
//                    if (DBWindow != null) {
//                        DefaultHours = DBWindow.getPartInWindow();
//                    }
//                }
//            }
//        });
////        refActiveCalendar.child(uid).child("DefaultWindow").addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                if(snapshot.exists()){
////                    WorkWindow DBWindow = snapshot.getValue(WorkWindow.class);
////                    if (DBWindow != null) {
////                        DefaultHours = DBWindow.getPartInWindow();
////                    }
////
////                }
//////                getDefaultHours(DefaultHours);
////            }
////            @Override
////            public void onCancelled(@NonNull DatabaseError error) {
////
////            }
////        });
//        return DefaultHours;
//    }
//
//    public static void setWindow(WeekViewActivity callback) {
//        String uid = "0";
//        ArrayList<String> e = new ArrayList<>(Arrays.asList("eror"));
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            uid = currentUser.getUid();
//        }
//        String sdate = Sdate(selectedDate);
//        refActiveCalendar.child(uid).child(sdate).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if(task.isSuccessful()){
//                    DataSnapshot ds = task.getResult();
//                    WorkWindow DBWindow = ds.getValue(WorkWindow.class);
//                    hours = DBWindow.getPartInWindow();
//                    callback.onWindowLoaded(hours);
//                }
//                else {
//                    hours = getDefaultHours();
//                    callback.onWindowLoaded2(hours);
//                }
//            }
//
//        });

//
//        refActiveCalendar.child(uid).child(sdate).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    WorkWindow DBWindow = snapshot.getValue(WorkWindow.class);
//                    if (DBWindow != null) {
//                        hours = DBWindow.getPartInWindow();
//                        callback.onWindowLoaded(hours, sdate);
//                    }
//                } else {
////                    hours = getDefaultHours();
////                    callback.onWindowLoaded2(hours);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Handle onCancelled
//            }
//        });
//
//        refActiveCalendar.child(uid).child("DefaultWindow").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if(task.isSuccessful()){
//                    DataSnapshot ds = task.getResult();
//                    WorkWindow DBWindow = ds.getValue(WorkWindow.class);
//                    if (DBWindow != null) {
//                        DefaultHours = DBWindow.getPartInWindow();
//                        callback.onWindowLoaded2(DefaultHours);
//                    }
//                } else {
//                    // Handle the failure
//                }
//            }
//        });
//        callback.onWindowLoaded(e,sdate);
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