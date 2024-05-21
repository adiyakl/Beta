package com.example.beta1;

import static com.example.beta1.CalendarUtils.STime;
import static com.example.beta1.CalendarUtils.Sdate;
import static com.example.beta1.CalendarUtils.daysInWeekArray;
import static com.example.beta1.CalendarUtils.monthYearFromDate;
import static com.example.beta1.CalendarUtils.selectedDate;

import static com.example.beta1.DBref.mAuth;
import static com.example.beta1.DBref.refActiveAppointments;
import static com.example.beta1.DBref.refActiveCalendar;


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
import com.google.firebase.database.DatabaseReference;

import java.time.LocalDate;
import java.util.ArrayList;
public class WeekViewActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener {
    private TextView monthYearText;
    AlertDialog.Builder logoutAlert;
    private String uid = "0";
    private RecyclerView calendarRecyclerView;
    private ListView l;
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    public ArrayList<String> hours = new ArrayList<>();
    Button bt1, bt2;
    TextView note;
    private String snote = "no notes at the moment";
    private String windowKey = "0";
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
        note= findViewById(R.id.noteTv);
       uid = DBref.uid;
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
        setList();
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arrayAdapter, View view, int i, long l) {
                if(hours.get(i).length()>5) {
                    Intent intent = new Intent(WeekViewActivity.this, AppDetails.class);
                    intent.putExtra("from", WeekViewActivity.class);
                    intent.putExtra("time", STime(hours.get(i)));
                    intent.putExtra("windowKey", windowKey);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(WeekViewActivity.this,"no appointment at this time",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public void setList() {
        note.setText(snote);
        DatabaseReference currentDateRef = refActiveAppointments.child(uid).child(Sdate(selectedDate)).child(windowKey);
        currentDateRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                    DataSnapshot ds = task.getResult();
                    for (DataSnapshot d :ds.getChildren()) {
                        Appointment app = d.getValue(Appointment.class);
                        for (int i = 0; i < hours.size(); i++) {
                            if (hours.get(i).equals(app.getTime())) {
                                hours.set(i,app.getTime()+":  "+ app.getName());
                            }
                        }
                    }

                }
                if(!task.getResult().exists()) {
                    Toast.makeText(WeekViewActivity.this,"no appointments here",Toast.LENGTH_SHORT).show();
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(WeekViewActivity.this, android.R.layout.simple_list_item_1, hours);
                l.setAdapter(arrayAdapter);
            }
        });

    }





    public void onItemClick(int position, LocalDate date) {
        CalendarUtils.selectedDate = date;
        setWeekView();
        setWindow();

    }

    public void setWindow(){
        hours.clear();
        String sdate  = Sdate(selectedDate);
        refActiveCalendar.child(uid).child(sdate).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(WeekViewActivity.this, android.R.layout.simple_list_item_1, hours);
                        l.setAdapter(arrayAdapter);
                        setList();
                    }
                }
                if(!task.getResult().exists()) {
                    refActiveCalendar.child(uid).child("DefaultWindow").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(WeekViewActivity.this, android.R.layout.simple_list_item_1, hours);
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