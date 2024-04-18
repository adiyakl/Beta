package com.example.beta1;

import static com.example.beta1.ChangeType.Sdate;
import static com.example.beta1.DBref.mAuth;
import static com.example.beta1.DBref.refActiveBusiness;
import static com.example.beta1.DBref.refActiveCalendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class WorkWeekDefinition extends AppCompatActivity {
    AlertDialog.Builder logoutAlert , delWAlert , mergD;
    TextView dayAndDate;
    EditText noteToClient;
    CheckBox cb;
    Button begTime, endTime;
    String SbegTime , SendTime , sdate;
    private String uid ="0";
    private ArrayList<String> partInWindow = new ArrayList<>();
    private ArrayList<String> NewPartInWindow= new ArrayList<>();
    private WorkWindow window ;
    private String note =" ";
    int hour, minute;
    LocalDate date;
    DayOfWeek day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_week_definition);
        dayAndDate = findViewById(R.id.dayAndDate);
        noteToClient = findViewById(R.id.noteToClient);
        begTime = findViewById(R.id.begTime);
        endTime = findViewById(R.id.endTime);
        cb = findViewById(R.id.cBD);
        date = LocalDate.now();
        day = date.getDayOfWeek();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        sdate = date.format(formatter);
        dayAndDate.setText(sdate+" "+day);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(cb.isChecked()){
                    cb.setText("only for this date");
                }
                else {
                    cb.setText("set as default");

                }
            }
        });
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            uid = currentUser.getUid();
        }
        DatabaseReference currentACal = refActiveCalendar.child(uid).child(Sdate(date));
        currentACal.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    WorkWindow DBWindow = snapshot.getValue(WorkWindow.class);
                    if (DBWindow != null) {
                        NewPartInWindow = DBWindow.getPartInWindow();
                        if(NewPartInWindow.isEmpty()){
                            begTime.setText("00:00");
                            endTime.setText("00:00");
                        }
                        else {
                            begTime.setText(NewPartInWindow.get(0));
                            endTime.setText(NewPartInWindow.get(NewPartInWindow.size() - 1));
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            uid = currentUser.getUid();
        }
        else{
            Toast.makeText(this,"current user is null",Toast.LENGTH_SHORT).show();
        }
        DatabaseReference currentACal = refActiveCalendar.child(uid);
        currentACal.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if dident set workwimdow yet
                if(!snapshot.exists()){
                    Toast.makeText(WorkWeekDefinition.this,Sdate(date),Toast.LENGTH_SHORT).show();
                    defaultWindow();
                }
                else {
                    Toast.makeText(WorkWeekDefinition.this,Sdate(date)+"you already in the system",Toast.LENGTH_SHORT).show();

               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
public void defaultWindow(){
    ArrayList<String> partInWindowDefault = new ArrayList<>(Arrays.asList("9:00","10:00","11:00","12:00","13:00","14:00"));
    begTime.setText("9:00");
    endTime.setText("14:00");
    window = new WorkWindow("default","",partInWindowDefault);
    refActiveCalendar.child(uid).child("DefaultWindow").setValue(window);
    Toast.makeText(this,"your system default: 9:00-14:00",Toast.LENGTH_SHORT).show();
}


    public void onSetWindow(View view) {
        //customwindow
        note = String.valueOf(noteToClient.getText());
        SbegTime =(String) begTime.getText();
        SendTime = (String) endTime.getText();
        if(SbegTime.equals("hh:mm")||SendTime.equals("hh:mm")){
            Toast.makeText(WorkWeekDefinition.this,"please enter fileds",Toast.LENGTH_SHORT).show();
            return;
        }
            LocalTime startTime = LocalTime.parse(SbegTime);
            LocalTime endTime = LocalTime.parse(SendTime);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
             // Initialize the ArrayList

            // Calculate the difference in hours between the two times
            int size = (int) ChronoUnit.HOURS.between(startTime, endTime);

            for(int i = 0; i < size; i++){
                partInWindow.add(startTime.format(formatter));
                startTime = startTime.plusHours(1);
            }



                    if(cb.isChecked()) {
                        window = new WorkWindow(Sdate(date), note, partInWindow);
                        checkIfWindowExist(window);

                    }
                    else {
                        if(partInWindow.isEmpty()){
                            Toast.makeText(WorkWeekDefinition.this,"default window set to be empty",Toast.LENGTH_SHORT).show();
                        }
                        window = new WorkWindow("cusDefault", note, partInWindow);
                        refActiveCalendar.child(uid).child("DefaultWindow").removeValue();
                        refActiveCalendar.child(uid).child("DefaultWindow").setValue(window);
                        Toast.makeText(WorkWeekDefinition.this, "youre defualt window is set", Toast.LENGTH_SHORT).show();
                    }

            }






    public void checkIfWindowExist(WorkWindow window){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            uid = currentUser.getUid();
        }
        DatabaseReference currentACal = refActiveCalendar.child(uid).child(Sdate(date));
        currentACal.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//              if date already have a window
                if(snapshot.exists()){
                    WorkWindow DBWindow = snapshot.getValue(WorkWindow.class);
                    if (DBWindow != null) {
                         NewPartInWindow = DBWindow.getPartInWindow();
                    }
                    showAD();
                }
                else {
                    refActiveCalendar.child(uid).child(Sdate(date)).setValue(window);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void showAD(){
//      change window
        delWAlert = new AlertDialog.Builder(this);
        delWAlert.setMessage("setting a new window cancel all of the appoinments on this date, are u suer?");
        delWAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                merD();

            }
        });
        delWAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog add = delWAlert.create();
        add.show();
    }

    public void merD(){
//      change window
        mergD = new AlertDialog.Builder(this);
        mergD.setMessage("want to merge window?");
        mergD.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ArrayList<String> lst = new ArrayList<>();
                for(int j =0;j<partInWindow.size();j++) {
                    if(!NewPartInWindow.contains(partInWindow.get(j))){
                        lst.add(partInWindow.get(j));
                    }
                }
                if(NewPartInWindow.isEmpty()){
                    lst.addAll(partInWindow);
                }
                else{lst.addAll(NewPartInWindow);}
                NewPartInWindow = ChangeType.sortTimes(lst);
                window = new WorkWindow(Sdate(date),note,NewPartInWindow);
                refActiveCalendar.child(uid).child(Sdate(date)).removeValue();
                refActiveCalendar.child(uid).child(Sdate(date)).setValue(window);

            }
        });
        mergD.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                refActiveCalendar.child(uid).child(Sdate(date)).removeValue();
                refActiveCalendar.child(uid).child(Sdate(date)).setValue(window);
            }
        });
        AlertDialog add = mergD.create();
        add.show();
    }


    public void popTimePickerBeg(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                begTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,onTimeSetListener, hour, minute, true);
        timePickerDialog.setTitle("Select beginning Time");
        timePickerDialog.show();
    }
    public void popTimePickerEnd(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                endTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,onTimeSetListener, hour, minute, true);
        timePickerDialog.setTitle("Select ending Time");
        timePickerDialog.show();
    }

    public void showDatePicker(View view) {
        int year = date.getYear();
        int month = date.getMonthValue() - 1; // Months are zero-based in DatePickerDialog
        int dayOfMonth = date.getDayOfMonth();

        // Create a DatePickerDialog and set the current date as default
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Update selectedDate with the chosen date
                date = LocalDate.of(year, month + 1, dayOfMonth); // Month is zero-based
                day = date.getDayOfWeek();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
                sdate = date.format(formatter);
                dayAndDate.setText(sdate+" "+day);

                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser != null){
                    uid = currentUser.getUid();
                }
                DatabaseReference currentACal = refActiveCalendar.child(uid).child(Sdate(date));
                currentACal.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            WorkWindow DBWindow = snapshot.getValue(WorkWindow.class);
                            if (DBWindow != null) {
                                NewPartInWindow = DBWindow.getPartInWindow();
                                if(NewPartInWindow.isEmpty()){
                                    begTime.setText("00:00");
                                    endTime.setText("00:00");
                                }
                                else {
                                    begTime.setText(NewPartInWindow.get(0));
                                    endTime.setText(NewPartInWindow.get(NewPartInWindow.size() - 1));
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }, year, month, dayOfMonth);

        // Show the DatePickerDialog
        datePickerDialog.show();

    }



    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manicurist_menu,menu);
        MenuItem item = menu.findItem(R.id.week_def);
        item.setVisible(false);
        this.invalidateOptionsMenu();
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mani_calnder) {
            //to calnder
        } else if (id == R.id.mani_main) {
            Intent intent= new Intent(this,MainActivityManicurist.class);
            startActivity(intent);
        }
        else if(id==R.id.ebusiness){
            Intent intent= new Intent(this,BusinessEditing.class);
            startActivity(intent);
        }
        else if(id==R.id.Mlogout){
            logoutAlert = new AlertDialog.Builder(this);
            logoutAlert.setMessage("Are you sure you want to logout?");
            logoutAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mAuth.signOut();
                    Intent intent = new Intent(WorkWeekDefinition.this,Login.class);
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