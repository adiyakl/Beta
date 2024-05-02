package com.example.beta1;

import static com.example.beta1.ChangeType.Odate;
import static com.example.beta1.ChangeType.Sdate;
import static com.example.beta1.ChangeType.isAfterOrToday;
import static com.example.beta1.DBref.mAuth;
import static com.example.beta1.DBref.refActiveAppointments;
import static com.example.beta1.DBref.refActiveBusiness;
import static com.example.beta1.DBref.refUsers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
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

public class MainActivityClient extends AppCompatActivity {

    TextView welcome , todayDate, appInfo, appM;
    String Sname;
    private User user = DBref.user;
    private boolean found = false;
    AlertDialog.Builder logoutAlert;
    public static String Muid = "0", sdate,appDate="",appTime="",appMani ="";
    private LocalDate date=LocalDate.now();
    public static Business thisbusiness = new Business();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_client);
        welcome = findViewById(R.id.welcomSign);
        appInfo = findViewById(R.id.appDate);
        todayDate = findViewById(R.id.todayDate);
        appM =findViewById(R.id.appM);
        if (user!=null) {
            Muid = user.getLinked();
            getBusiness();
        }
        sdate = Sdate(date);
        todayDate.setText(Odate(sdate));
        getApp();


    }

    public void getApp(){
        final ProgressDialog pd = ProgressDialog.show(this, "Loading data", "Loading...", true);
        refActiveAppointments.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()&&task.getResult().exists()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    for(DataSnapshot Muids : dataSnapshot.getChildren()) {
                        for (DataSnapshot dates : Muids.getChildren()) {
                                for (DataSnapshot wind : dates.getChildren()) {
                                    String adate = dates.getKey().toString();
                                    if (isAfterOrToday(adate, date)) {
                                    for (DataSnapshot times : wind.getChildren()) {
                                        Appointment app = times.getValue(Appointment.class);
                                        if (app.getCuid().equals(DBref.uid)&&found!=true) {
                                            found = true;
                                            appDate = app.getDate();
                                            appTime = app.getTime();
                                            appMani =app.getMuid();
                                            getBusinessName();
                                        }
                                        else if (found==false){
                                            appDate = " ";
                                            appTime = "no appointments in the future:(";

                                        }
                                        appInfo.setText(Odate(appDate)+"  "+appTime);
                                        pd.dismiss();
                                    }
                                }
                                    else if(found==false){
                                        appDate = " ";
                                        appTime = "no appointments in the future :(";
                                        appInfo.setText(appDate+"  "+appTime);
                                        pd.dismiss();
                                    }
                            }

                        }

                    }
                }
                else {
                    Toast.makeText(MainActivityClient.this, "task dosent exist", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }
        });
    }
    public static void getBusiness(){
        if(Muid!=null) {
            DatabaseReference currentuser = refActiveBusiness.child(Muid);
            currentuser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        thisbusiness = snapshot.getValue(Business.class);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }
    public void getBusinessName(){
        final ProgressDialog pd = ProgressDialog.show(this, "Loading data", "Loading...", true);
        if(appMani!=null) {
            DatabaseReference currentuser = refActiveBusiness.child(appMani);
            currentuser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Business b = snapshot.getValue(Business.class);
                        appM.setText(b.getName());
                        pd.dismiss();
                    }
                    else {
                        pd.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("stayConnect", true);
        editor.commit();
        if (user!=null) {
            Sname = user.getName();
            welcome.setText("hello " + Sname + " !");
        }
}




    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.client_menu,menu);
        MenuItem item = menu.findItem(R.id.client_main);
        item.setVisible(false);
        MenuItem i = menu.findItem(R.id.client_calnder);
        if(user==null){
            i.setVisible(false);
        }
        if(user!=null){
            i.setVisible(true);
        }
        this.invalidateOptionsMenu();
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.client_calnder) {
            Intent intent = new Intent(this,CalendarClient.class);
            startActivity(intent);
        } else if (id == R.id.choice) {
            Intent intent = new Intent(this,ChoosingABusiness.class);
            startActivity(intent);
        }
        else if(id==R.id.Clogout){
            logoutAlert = new AlertDialog.Builder(this);
            logoutAlert.setMessage("Are you sure you want to logout?");
            logoutAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mAuth.signOut(); 
                    Intent intent = new Intent(MainActivityClient.this,Login.class);
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