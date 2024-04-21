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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CalendarClient extends AppCompatActivity implements CalendarAdapter.OnItemListener {
    private TextView monthYearText;
    AlertDialog.Builder logoutAlert;
    private String uid = "0";
    private RecyclerView calendarRecyclerView;
    private ListView l;
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    public ArrayList<String> hours = new ArrayList<>();
    Button bt1, bt2;
    TextView note,headline;
    private String snote = "no notes at the moment";
    private String windowKey = "0";
    private String Cuid ="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_calendar);
        calendarRecyclerView = findViewById(R.id.calRecycleView);
        monthYearText = findViewById(R.id.monthYear);
        l = findViewById(R.id.alist);
        bt1 = findViewById(R.id.bt1);
        bt2 = findViewById(R.id.bt2);
        bt1.setText("<--");
        bt2.setText("-->");
        note = findViewById(R.id.noteTv);
        headline =findViewById(R.id.headline);
        if ( currentUser!= null) {
            Cuid = currentUser.getUid();
        }


        CalendarUtils.selectedDate = LocalDate.now();
        setWeekView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getBusinessUid();
        setHeadLine();
//        CompletableFuture.supplyAsync(this::getBusinessUid)
//                .thenAccept(this::onUidLoaded);
    }

    public void onUidLoaded(String uid){
        this.uid =uid;
        setHeadLine();

    }
    public String getBusinessUid(){
        CompletableFuture<String> uidfuture = new CompletableFuture<>();
        DatabaseReference currentuser = refUsers.child(Cuid);
        currentuser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User user = snapshot.getValue(User.class);
                    uid = user.getLinked();
                    uidfuture.complete(uid); // Completing the future with the UID value
                } else {
                    uidfuture.complete("0"); // Completing with default value when snapshot doesn't exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                uidfuture.completeExceptionally(error.toException()); // Completing exceptionally if there's an error
            }
        });
        try {
            return uidfuture.get(); // Waits until the future is complete
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return "0"; // or handle exception accordingly
        }
    }
    public void setHeadLine(){
        refActiveBusiness.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Business b = snapshot.getValue(Business.class);
                    headline.setText(b.getName()+" "+"calendar!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void setWeekView() {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);
        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        CompletableFuture.supplyAsync(this::setWindow1)
                .thenAccept(this::onWindowLoaded);
        setList();
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
        DatabaseReference currentDateRef = refActiveAppointments.child(uid).child(Sdate(selectedDate)).child(windowKey);
        currentDateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Iterate over each child of the snapshot
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        Appointment app = childSnapshot.getValue(Appointment.class);
                        // Check if the appointment date matches the selected date
                        if (Sdate(selectedDate).equals(app.getDate())) {
                            for (int i = 0; i < hours.size(); i++) {
                                // Check if the hour matches the appointment time
                                if (hours.get(i).equals(app.getTime())) {
                                    // Update the text for the matched hour
                                    hours.set(i, app.getTime() + "  " + app.getText());
                                }
                            }
                        }
                    }
                } else {


                }

                // Update the ListView with the updated hours list
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(CalendarClient.this, android.R.layout.simple_list_item_1, hours);
                l.setAdapter(arrayAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void setAp(LocalDate date, String hour, int index) {
        String sdate = Sdate(date);
        // check double booked
        if (hour.length() == 5) {
            Appointment app = new Appointment(hour, sdate, "lo chrih at dani");

            refActiveAppointments.child(uid).child(sdate).child(windowKey).child(hour).setValue(app);
            setWeekView();
        } else {
            Toast.makeText(CalendarClient.this, "this hour is unavailable", Toast.LENGTH_SHORT).show();
        }
    }


    public void onItemClick(int position, LocalDate date) {
        CalendarUtils.selectedDate = date;
        setWeekView();
        CompletableFuture.supplyAsync(this::setWindow1)
                .thenAccept(this::onWindowLoaded);

    }

    public void onWindowLoaded(WorkWindow window) {
        this.hours = window.getPartInWindow();
        this.snote = window.getNote();
        this.windowKey = window.getwKey();
        setList();

    }

    public WorkWindow setWindow1() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            uid = currentUser.getUid();
        }
        String sdate = Sdate(selectedDate);

        CompletableFuture<WorkWindow> future = new CompletableFuture<>();

        refActiveCalendar.child(uid).child(sdate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    WorkWindow DBWindow = snapshot.getValue(WorkWindow.class);
                    if (DBWindow != null) {
                        snote = DBWindow.getNote();
                        hours = DBWindow.getPartInWindow();
                        windowKey = DBWindow.getwKey();
                        future.complete(new WorkWindow(windowKey, snote, hours));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });

        refActiveCalendar.child(uid).child("DefaultWindow").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot ds = task.getResult();
                    WorkWindow DBWindow = ds.getValue(WorkWindow.class);
                    if (DBWindow != null && !future.isDone()) { // check if future is already completed
                        snote = DBWindow.getNote();
                        hours = DBWindow.getPartInWindow();
                        windowKey = DBWindow.getwKey();
                        future.complete(new WorkWindow(windowKey, snote, hours));
                    } else {
                        if (!future.isDone()) {
                            future.complete(new WorkWindow("", "", new ArrayList<>()));
                        }
                    }
                } else {
                    future.completeExceptionally(task.getException());
                }
            }
        });

        try {
            return future.get(); // waits until future is complete
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return new WorkWindow("", "", new ArrayList<>()); // or handle exception accordingly
        }
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
        MenuItem item = menu.findItem(R.id.client_main);
        item.setVisible(false);
        this.invalidateOptionsMenu();
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.client_main) {
            Intent intent= new Intent(CalendarClient.this,MainActivityClient.class);
            startActivity(intent);
        } else if (id == R.id.choice) {
            Intent intent= new Intent(CalendarClient.this, ChoosingABusiness.class);
            startActivity(intent);
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


