package com.example.beta1;

import static com.example.beta1.CalendarUtils.daysInWeekArray;
import static com.example.beta1.CalendarUtils.monthYearFromDate;
import static com.example.beta1.CalendarUtils.selectedDate;
import static com.example.beta1.ChangeType.Sdate;
import static com.example.beta1.DBref.mAuth;
import static com.example.beta1.DBref.refActiveAppointments;
import static com.example.beta1.DBref.refActiveBusiness;
import static com.example.beta1.DBref.refActiveCalendar;
import static com.example.beta1.DBref.refUsers;
import static com.example.beta1.MainActivityClient.thisbusiness;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.security.auth.callback.Callback;


public class CalendarClient extends AppCompatActivity implements CalendarAdapter.OnItemListener {
    private TextView monthYearText;
    AlertDialog.Builder logoutAlert;
    private String Muid = "0";
    private RecyclerView calendarRecyclerView;
    private ListView l;
    private String busnam  = "0";
    public static ArrayList<String> hours = new ArrayList<>();
    Button bt1, bt2;
    TextView note,headline;
    public static String snote = "no notes at the moment";
    public static String windowKey = "DefaultWindow";
    public static ArrayAdapter<String> arrayAdapter1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_calendar);
        calendarRecyclerView = findViewById(R.id.calRecycleView);
        monthYearText = findViewById(R.id.monthYear);
        l = findViewById(R.id.Clist);
        bt1 = findViewById(R.id.bt1);
        bt2 = findViewById(R.id.bt2);
        bt1.setText("<--");
        bt2.setText("-->");
        note = findViewById(R.id.noteTv);
        headline =findViewById(R.id.headline);
        arrayAdapter1 = new ArrayAdapter<>(CalendarClient.this, android.R.layout.simple_list_item_1, hours);
        l.setAdapter(arrayAdapter1);

        Muid =MainActivityClient.Muid;
        busnam = thisbusiness.getName();
        headline.setText(busnam+"  "+"calendar!");
        CalendarUtils.selectedDate = LocalDate.now();
        setWeekView();
    }


    private void setWeekView() {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);
        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        setWindow();
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arrayAdapter, View view, int i, long l) {
                String hourFromArray = arrayAdapter.getItemAtPosition(i).toString();
                setAp(selectedDate, hourFromArray, i);
            }
        });

    }

    public void setList() {
        note.setText(snote);
        DatabaseReference currentDateRef = refActiveAppointments.child(Muid).child(Sdate(selectedDate)).child(windowKey);
        currentDateRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                    DataSnapshot ds = task.getResult();
                    for (DataSnapshot d :ds.getChildren()) {
                        Appointment app = d.getValue(Appointment.class);
                        for (int i = 0; i < hours.size(); i++) {
                            if (hours.get(i).equals(app.getTime())) {
                                if(app.getCuid().equals(DBref.uid)){
                                    hours.set(i, "your appointment!");
                                }
                                else {
                                    hours.set(i, "taken");
                                }
                            }

                        }
                    }

                }
//                if(!task.getResult().exists()) {
//                   Toast.makeText(CalendarClient.this,"no appointments here",Toast.LENGTH_SHORT).show();
//                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(CalendarClient.this, android.R.layout.simple_list_item_1, hours);
                l.setAdapter(arrayAdapter);
            }
        });

    }

    public void setAp(LocalDate date, String hour, int index) {
        String sdate = Sdate(date);
        // check double booked
        if (!hour.equals("taken")) {
            Intent intent = new Intent(CalendarClient.this, AppointmentSet.class);
            intent.putExtra("date",sdate);
            intent.putExtra("hour",hour);
            intent.putExtra("windowKey",windowKey);
            startActivity(intent);
//            refActiveAppointments.child(Muid).child(sdate).child(windowKey).child(hour).setValue(app);
            setWeekView();
        } else {
            Toast.makeText(CalendarClient.this, "this hour is unavailable", Toast.LENGTH_SHORT).show();
        }
    }


    public void onItemClick(int position, LocalDate date) {
        CalendarUtils.selectedDate = date;
        setWeekView();
        setWindow();


    }

public void setWindow(){
    hours.clear();
    String sdate  = Sdate(selectedDate);
    refActiveCalendar.child(Muid).child(sdate).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DataSnapshot> task) {
            if (task.isSuccessful()) {
                WorkWindow DBWindow = task.getResult().getValue(WorkWindow.class);
                if (DBWindow != null) {
                    snote = DBWindow.getNote();
                    if(snote.isEmpty()){
                        snote = "no notes at the moment";
                    }
                    hours = DBWindow.getPartInWindow();
                    windowKey = DBWindow.getwKey();
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(CalendarClient.this, android.R.layout.simple_list_item_1, hours);
                    l.setAdapter(arrayAdapter);
                    setList();
                    }
                }
            if(!task.getResult().exists()) {
                refActiveCalendar.child(Muid).child("DefaultWindow").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            WorkWindow DBWindow = task.getResult().getValue(WorkWindow.class);
                            if (DBWindow != null) {
                   snote = DBWindow.getNote();
                   if(snote.isEmpty()){
                        snote = "no notes at the moment";
                    }
                                hours = DBWindow.getPartInWindow();
                                windowKey = DBWindow.getwKey();
                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(CalendarClient.this, android.R.layout.simple_list_item_1, hours);
                                l.setAdapter(arrayAdapter);
                                setList();
                            }
                        }

                    }
                });
            }

        }
    });

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
        getMenuInflater().inflate(R.menu.client_menu,menu);
        MenuItem item = menu.findItem(R.id.client_calnder);
        item.setVisible(false);
        this.invalidateOptionsMenu();
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.client_main) {
            Intent intent= new Intent(CalendarClient.this,MainActivityClient.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.choice) {
            Intent intent= new Intent(CalendarClient.this, ChoosingABusiness.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.Clogout){
            logoutAlert = new AlertDialog.Builder(this);
            logoutAlert.setMessage("Are you sure you want to logout?");
            logoutAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    mAuth.signOut();
                    Intent intent = new Intent(CalendarClient.this,Login.class);
                    startActivity(intent);
                    finish();
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


