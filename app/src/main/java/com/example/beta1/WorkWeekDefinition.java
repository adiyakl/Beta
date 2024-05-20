package com.example.beta1;

import static com.example.beta1.CalendarUtils.DefaultHours;
import static com.example.beta1.CalendarUtils.selectedDate;
import static com.example.beta1.ChangeType.Sdate;
import static com.example.beta1.DBref.mAuth;
import static com.example.beta1.DBref.refActiveAppointments;
import static com.example.beta1.DBref.refActiveBusiness;
import static com.example.beta1.DBref.refActiveCalendar;
import static com.example.beta1.DBref.refPic;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

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
    private String uid =DBref.uid;
    private ArrayList<String> partInWindow = new ArrayList<>();
    private ArrayList<String> NewPartInWindow= new ArrayList<>();
    private WorkWindow window = new WorkWindow("","",new ArrayList<>());
    private String note =" ";
    private int size = 0;
    int hour, minute;
    LocalDate date = LocalDate.now();
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
        setHours();


    }


    public void setHours(){
        final ProgressDialog pd = ProgressDialog.show(this, "loading data", "loading...", true);
        DatabaseReference currentACal = refActiveCalendar.child(uid);
        currentACal.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds : snapshot.getChildren()){
                        if(snapshot.child(Sdate(date)).exists()) {
                            // spec day window
                            WorkWindow DBWindow =snapshot.child(Sdate(date)).getValue(WorkWindow.class);
                            if (DBWindow != null) {
                                NewPartInWindow = DBWindow.getPartInWindow();
                                if (NewPartInWindow.isEmpty()) {
                                    begTime.setText("00:00");
                                    endTime.setText("00:00");
                                    pd.dismiss();

                                } else {
                                    begTime.setText(NewPartInWindow.get(0));
                                    endTime.setText(NewPartInWindow.get(NewPartInWindow.size() - 1));
                                    pd.dismiss();

                                }


                            }

                        }
                        else{
                            //def window
                            WorkWindow DBWindow = snapshot.child("DefaultWindow").getValue(WorkWindow.class);
                            if (DBWindow != null) {
                                NewPartInWindow = DBWindow.getPartInWindow();
                                if (NewPartInWindow.isEmpty()) {
                                    begTime.setText("00:00");
                                    endTime.setText("00:00");
                                } else {
                                    begTime.setText(NewPartInWindow.get(0));
                                    endTime.setText(NewPartInWindow.get(NewPartInWindow.size() - 1));
                                }
                            }
                            pd.dismiss();

                        }
                    }



                }
                //                if dident set workwimdow yet
                else {
                    if(!snapshot.exists()){
                        pd.dismiss();
                        Toast.makeText(WorkWeekDefinition.this,Sdate(date),Toast.LENGTH_SHORT).show();
                        defaultWindow();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
public void defaultWindow(){
    ArrayList<String> partInWindowDefault = new ArrayList<>(Arrays.asList("sorry we don't work today"));
    window = new WorkWindow("DefaultWindow","default",partInWindowDefault);
    refActiveCalendar.child(uid).child("DefaultWindow").setValue(window);
    Toast.makeText(this,"your system default: sorry we don't work today",Toast.LENGTH_SHORT).show();

}


    public void onSetWindow(View view) {
        partInWindow.clear();
        NewPartInWindow.clear();
        //customwindow
        note = String.valueOf(noteToClient.getText());
        SbegTime =(String) begTime.getText();
        SendTime = (String) endTime.getText();
        if(SbegTime.equals("hh:mm")||SendTime.equals("hh:mm")){
            Toast.makeText(WorkWeekDefinition.this,"please enter fileds",Toast.LENGTH_SHORT).show();
            return;
        }
        size = 0;
        if(SendTime.equals("00:00")){
            SendTime = "23:00";
            size++;
        }
            LocalTime startTime = LocalTime.parse(SbegTime);
            LocalTime endTime = LocalTime.parse(SendTime);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
             // Initialize the ArrayList

            // Calculate the difference in hours between the two times

             size = size+ (int) ChronoUnit.HOURS.between(startTime, endTime);

            for(int i = 0; i < size; i++){
                partInWindow.add(startTime.format(formatter));
                startTime = startTime.plusHours(1);
            }




                    if(cb.isChecked()) {
                        window = new WorkWindow("forDate",note, partInWindow);
                        showAD();
//                        checkIfWindowExist(window);

                    }
                    else {
                        if(partInWindow.isEmpty()){
                            Toast.makeText(WorkWeekDefinition.this,"default window set to be empty",Toast.LENGTH_SHORT).show();
                        }
                        showAD();

                    }

            }





    public void showAD(){
//      change window
        delWAlert = new AlertDialog.Builder(this);
        if(cb.isChecked()) {
            delWAlert.setMessage("setting a new window cancel all of the appointments on this date, are u suer?");
        }
        if(!cb.isChecked()){
            delWAlert.setMessage("setting a new default window will cancel all of the appointments that have been established under the default window. are u sure? ");
        }
        delWAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (cb.isChecked()){
                    refActiveCalendar.child(uid).child(Sdate(date)).removeValue();
                    refActiveAppointments.child(uid).child(Sdate(date)).removeValue();
                    Toast.makeText(WorkWeekDefinition.this, partInWindow.toString(), Toast.LENGTH_SHORT).show();
                    window = new WorkWindow("forDate", note, partInWindow);
                    refActiveCalendar.child(uid).child(Sdate(date)).setValue(window);
                    if (!partInWindow.isEmpty()) {
                        String b = partInWindow.get(0);
                        String e = partInWindow.get(partInWindow.size() - 1);
                        Toast.makeText(WorkWeekDefinition.this, "your window has been set to: " + b + "-" + e, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(WorkWeekDefinition.this, "your window has been set to be empty ", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    window = new WorkWindow("DefaultWindow", note, partInWindow);
                    refActiveCalendar.child(uid).child("DefaultWindow").removeValue();
                    refActiveCalendar.child(uid).child("DefaultWindow").setValue(window);
//                    DatabaseReference refActiveCalendar = FirebaseDatabase.getInstance().getReference().child("Active calendars");
                    refActiveAppointments.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                                    String date = dateSnapshot.getKey(); // Get the key of each date
                                    // Check if the child of date contains "DefaultWindow"
                                    if (dateSnapshot.child("DefaultWindow").exists()) {
                                        // Remove the value under "DefaultWindow" for this date
                                        dateSnapshot.child("DefaultWindow").getRef().removeValue();
                                    }
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    if(!partInWindow.isEmpty()) {
                        String b = partInWindow.get(0);
                        String e = partInWindow.get(partInWindow.size() - 1);
                        Toast.makeText(WorkWeekDefinition.this, "your window has been set to: " + b + "-" + e, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(WorkWeekDefinition.this, "your window has been set to be empty " , Toast.LENGTH_SHORT).show();

                    } }

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

//    public void merD(){
////      change window
//        mergD = new AlertDialog.Builder(this);
//        mergD.setMessage("want to merge window?");
//        mergD.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                ArrayList<String> lst = new ArrayList<>();
//                for(int j =0;j<partInWindow.size();j++) {
//                    if(!NewPartInWindow.contains(partInWindow.get(j))){
//                        lst.add(partInWindow.get(j));
//                    }
//                }
//                if(NewPartInWindow.isEmpty()){
//                    lst.addAll(partInWindow);
//                }
//                else{lst.addAll(NewPartInWindow);}
//                NewPartInWindow = ChangeType.sortTimes(lst);
//                window = new WorkWindow("forDate",note,NewPartInWindow);
//                refActiveAppointments.child(uid).child(Sdate(date)).removeValue();
//                refActiveCalendar.child(uid).child(Sdate(date)).removeValue();
//                refActiveCalendar.child(uid).child(Sdate(date)).setValue(window);
//                if(!partInWindow.isEmpty()) {
//                    String b = partInWindow.get(0);
//                    String e = partInWindow.get(partInWindow.size() - 1);
//                    Toast.makeText(WorkWeekDefinition.this, "your window has been set to: " + b + "-" + e, Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    Toast.makeText(WorkWeekDefinition.this, "your window has been set to be empty " , Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        });
//        mergD.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                refActiveCalendar.child(uid).child(Sdate(date)).removeValue();
//                refActiveAppointments.child(uid).child(Sdate(date)).removeValue();
//                Toast.makeText(WorkWeekDefinition.this, partInWindow.toString(), Toast.LENGTH_SHORT).show();
//                window = new WorkWindow("forDate", note, partInWindow);
//                refActiveCalendar.child(uid).child(Sdate(date)).setValue(window);
//                if (!partInWindow.isEmpty()) {
//                    String b = partInWindow.get(0);
//                    String e = partInWindow.get(partInWindow.size() - 1);
//                    Toast.makeText(WorkWeekDefinition.this, "your window has been set to: " + b + "-" + e, Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(WorkWeekDefinition.this, "your window has been set to be empty ", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//        AlertDialog add = mergD.create();
//        add.show();
//    }


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
                setHours();

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
            Intent intent = new Intent(this,WeekViewActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.mani_main) {
            Intent intent= new Intent(this,MainActivityManicurist.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.ebusiness){
            Intent intent= new Intent(this,BusinessEditing.class);
            startActivity(intent);
            finish();
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