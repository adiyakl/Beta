package com.example.beta1;

import static android.content.ContentValues.TAG;
import static com.example.beta1.CalendarUtils.daysInWeekArray;
import static com.example.beta1.CalendarUtils.monthYearFromDate;
import static com.example.beta1.CalendarUtils.selectedDate;
import static com.example.beta1.ChangeType.STime;
import static com.example.beta1.ChangeType.Sdate;
import static com.example.beta1.DBref.mAuth;
import static com.example.beta1.DBref.refActiveAppointments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class WeekViewActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener {
    private TextView monthYearText;
    AlertDialog.Builder logoutAlert;
    private String uid = "0";
    private RecyclerView calendarRecyclerView;
    private ListView l;
    public ArrayList<String> hours = new ArrayList<>();
    Button bt1, bt2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);
        calendarRecyclerView = findViewById(R.id.calRecycleView);
        monthYearText= findViewById(R.id.monthYear);
        l = findViewById(R.id.alist);
        bt1 = findViewById(R.id.bt1);
        bt2 = findViewById(R.id.bt2);
        bt1.setText("<--");
        bt2.setText("-->");
        CalendarUtils.selectedDate = LocalDate.now();
        hours = new ArrayList<>(Arrays.asList("9:00","10:00","11:00","12:00","13:00","14:00"));
        setWeekView();

    }


    private void setWeekView() {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);
        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        setList();
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arrayAdapter, View view, int i, long l) {
                String hourFromArray = arrayAdapter.getItemAtPosition(i).toString();
                setAp(selectedDate, hourFromArray);

            }
        });

    }
    public void setList(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            uid = currentUser.getUid();
        }
        DatabaseReference currentDateRef = refActiveAppointments.child(uid).child(Sdate(selectedDate));
        currentDateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Iterate over each child of the snapshot
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        Appointment app = childSnapshot.getValue(Appointment.class);
                        // Check if the appointment date matches the selected date
                        if (Sdate(selectedDate).equals(app.getDate())) {
                            for (int i = 0; i < 6; i++) {
                                // Check if the hour matches the appointment time
                                if (hours.get(i).equals(app.getTime())) {
                                    // Update the text for the matched hour
                                    hours.set(i, app.getText() + " " + app.getTime());
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(WeekViewActivity.this, "No data exists at the specified location", Toast.LENGTH_SHORT).show();
                }
                // Update the ListView with the updated hours list
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(WeekViewActivity.this, android.R.layout.simple_list_item_1, hours);
                l.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeekViewActivity.this, "Database operation cancelled", Toast.LENGTH_SHORT).show();
            }
        });

    }
//    public boolean checkAvailable(Appointment appointment){
//        boolean isAvailable =true;
//
//        return isAvailable;
//    }


    public void setAp (LocalDate date, String hour){
        String sdate = Sdate(date);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            uid = currentUser.getUid();
        }
        // check double booked
        if(hour.length()==5){
            Appointment app = new Appointment(hour, sdate, "lo chrih at dani");
            refActiveAppointments.child(uid).child(sdate).child(hour).setValue(app);
            setWeekView();
        }
        else {
            Toast.makeText(WeekViewActivity.this, "this hour is unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    public void onItemClick(int position, LocalDate date) {
        CalendarUtils.selectedDate = date;
        setWeekView();
        String message = "Selected Date " + Sdate(date) + " " + monthYearFromDate(selectedDate);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        hours = new ArrayList<>(Arrays.asList("9:00","10:00","11:00","12:00","13:00","14:00"));
        setList();

    }

    public void previousWeekAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
    }

    public void nextWeekAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
    }



    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manicurist_menu,menu);
        MenuItem item = menu.findItem(R.id.mani_calnder);
        item.setVisible(false);
        this.invalidateOptionsMenu();
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mani_main) {
            Intent intent = new Intent(this,MainActivityManicurist.class);
            startActivity(intent);
        } else if (id == R.id.ebusiness) {
            Intent intent= new Intent(this,BusinessEditing.class);
            startActivity(intent);
        }
        else if(id==R.id.week_def){
            Intent intent= new Intent(this,WorkWeekDefinition.class);
            startActivity(intent);
        }
        else if(id==R.id.Mlogout){
            logoutAlert = new AlertDialog.Builder(this);
            logoutAlert.setMessage("Are you sure you want to logout?");
            logoutAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mAuth.signOut();
                    Intent intent = new Intent(WeekViewActivity.this,Login.class);
                    startActivity(intent);
                }
            });
            logoutAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog ad = logoutAlert.create();
            ad.show();
        }
        return super.onOptionsItemSelected(item);
    }
}