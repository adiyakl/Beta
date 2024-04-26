package com.example.beta1;

import static com.example.beta1.CalendarUtils.selectedDate;
import static com.example.beta1.ChangeType.STime;
import static com.example.beta1.ChangeType.Sdate;
import static com.example.beta1.DBref.mAuth;
import static com.example.beta1.DBref.refActiveAppointments;
import static com.example.beta1.DBref.refActiveCalendar;
import static com.example.beta1.DBref.refUsers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.LocaleData;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Locale;

public class MainActivityManicurist extends AppCompatActivity {

    TextView welcome, wind ;
    String Sname;
    private User user = DBref.user;
    private WorkWindow window;
    private ListView l;
    private LocalDate date=LocalDate.now();
    private String uid =DBref.uid;
    ArrayList<String> appo = new ArrayList<>(Arrays.asList("","","","",""));
    AlertDialog.Builder logoutAlert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_manicurist);
        l = findViewById(R.id.l);
        wind = findViewById(R.id.wind);
        welcome = findViewById(R.id.welcomSign);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivityManicurist.this, android.R.layout.simple_list_item_1, appo);
        l.setAdapter(arrayAdapter);
        appo.clear();
        getList();
        getWind();

    }
    public void getWind(){
        final ProgressDialog pd = ProgressDialog.show(this, "Loading list", "Loading...", true);
        DatabaseReference currentDateRef = refActiveCalendar.child(uid).child(Sdate(date));
        currentDateRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                     window = task.getResult().getValue(WorkWindow.class);
                    String txt= "your working window for today: "+window.partInWindow.get(0)+"- "+window.partInWindow.get(window.partInWindow.size()-2);
                    wind.setText(txt);
                }
                else {
                    refActiveCalendar.child(uid).child("DefaultWindow").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                 window = task.getResult().getValue(WorkWindow.class);
                                String txt= "your working window for today: "+window.partInWindow.get(0)+"- "+window.partInWindow.get(window.partInWindow.size()-2);
                                wind.setText(txt);
                            }
                        }
                    });
                    pd.dismiss();
                }}
        });
    }
    public void getList(){
        final ProgressDialog pd = ProgressDialog.show(this, "Loading list", "Loading...", true);
        DatabaseReference currentDateRef = refActiveAppointments.child(uid).child(Sdate(date));
        currentDateRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                    DataSnapshot ds = task.getResult();
                    for (DataSnapshot d :ds.getChildren()) {
                        for (DataSnapshot s: d.getChildren()) {
                            Appointment app = s.getValue(Appointment.class);
                            appo.add(app.getTime()+":  "+app.getName());
                        }
                    }

                }
                if(!task.getResult().exists()) {
                    Toast.makeText(MainActivityManicurist.this,"no appointments here",Toast.LENGTH_SHORT).show();
                }
                if(appo.isEmpty()){
                    appo.add("no appointments for today");
                }
                pd.dismiss();
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivityManicurist.this, android.R.layout.simple_list_item_1, appo);                 l.setAdapter(arrayAdapter);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("stayConnect", true);
        editor.commit();
        Sname = user.getName();
        welcome.setText("hey "+Sname+" !");
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arrayAdapter, View view, int i, long l) {
                    Intent intent = new Intent(MainActivityManicurist.this, AppDetails.class);
                    intent.putExtra("from", MainActivityManicurist.class);
                    intent.putExtra("time", STime(appo.get(i)));
                    intent.putExtra("windowKey", window.getwKey());
                    startActivity(intent);
                }


        });
        }




    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manicurist_menu,menu);
        MenuItem item = menu.findItem(R.id.mani_main);
        item.setVisible(false);
        this.invalidateOptionsMenu();
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mani_calnder) {
            Intent intent = new Intent(this,WeekViewActivity.class);
            startActivity(intent);
        } else if (id == R.id.ebusiness) {
            Intent intent= new Intent(MainActivityManicurist.this,BusinessEditing.class);
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
                    DBref.uid = "";
                    DBref.user = null;
                    Intent intent = new Intent(MainActivityManicurist.this,Login.class);
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