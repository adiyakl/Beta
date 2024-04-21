package com.example.beta1;

import static com.example.beta1.DBref.mAuth;
import static com.example.beta1.DBref.refActiveBusiness;
import static com.example.beta1.DBref.refActiveCalendar;
import static com.example.beta1.DBref.refUsers;
import static com.example.beta1.MainActivityClient.thisUser;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ChoosingABusiness extends AppCompatActivity {
    ListView list;
    AlertDialog.Builder logoutAlert;
    private ArrayList<String> businessesList = new ArrayList<>();
    private ArrayList<Business> businesses = new ArrayList<>();
    public static String businessName ="";
    TextView busName,busServ,busAdres,busPhone,busMani, loadingIcon;
    private Business b = new Business("","","","","","");
    static User u;
    boolean showingLoad = false;
    private static boolean clicked =false;
    private String uid = "0";
    private  String linkUid;
    Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosing_abusiness);
        list = findViewById(R.id.listview);
        busName = findViewById(R.id.buName);
        busServ = findViewById(R.id.busServ);
        busPhone = findViewById(R.id.busPhone);
        busAdres = findViewById(R.id.busAddre);
        busMani = findViewById(R.id.busMani);
        bt = findViewById(R.id.select);
        loadingIcon = findViewById(R.id.loading);
        loadingIcon.setText("Loding...");
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(showingLoad ){
                    Toast.makeText(ChoosingABusiness.this,"please wait for businesses to show up",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(clicked ==false){
                    Toast.makeText(ChoosingABusiness.this,"please choose a business",Toast.LENGTH_SHORT).show();
                    return;
                }
                setLinked1();
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Business b = businesses.get(i);
                linkUid = b.getUid();
                bringData(b);
                clicked = true;
            }
        });
        createList();
    }

            public void setLinked1(){
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser != null){
                    uid = currentUser.getUid();
                }
                refUsers.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            User user = snapshot.getValue(User.class);
                            user.setLinked(linkUid);
                            refUsers.child(uid).setValue(user);

                            Toast.makeText(ChoosingABusiness.this,"updated",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ChoosingABusiness.this,MainActivityClient.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            public void bringData(Business b) {

                busName.setText(b.getName());
                busPhone.setText(b.getPhone());
                busAdres.setText(b.getAdress());
                busServ.setText(b.getServices());

                refUsers.child(b.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            u = snapshot.getValue(User.class);
                            if (u != null) {
                                busMani.setText(u.getName());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
            }


    public void createList() {

        loadingIcon.setVisibility(View.VISIBLE);
        showingLoad =true;
            refActiveBusiness.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot businessSnapshot : snapshot.getChildren()) {
                            Business business = businessSnapshot.getValue(Business.class);
                            if (business != null) {
                                businessesList.add(business.getName());
                                businesses.add(business);
                            }
                        }

                    }
                    loadingIcon.setVisibility(View.GONE);
                    showingLoad =false;
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ChoosingABusiness.this, android.R.layout.simple_list_item_1, businessesList);
                    list.setAdapter(arrayAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });


    }





    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.client_menu,menu);
        MenuItem item = menu.findItem(R.id.choice);
        item.setVisible(false);
        MenuItem i = menu.findItem(R.id.client_calnder);

        this.invalidateOptionsMenu();
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.client_calnder) {
            Intent intent = new Intent(ChoosingABusiness.this,CalendarClient.class);
            startActivity(intent);
        } else if (id == R.id.client_main) {
            Intent intent = new Intent(ChoosingABusiness.this,CalendarClient.class);
            startActivity(intent);
        }
        else if(id==R.id.Clogout){
            logoutAlert = new AlertDialog.Builder(this);
            logoutAlert.setMessage("Are you sure you want to logout?");
            logoutAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mAuth.signOut();
                    Intent intent = new Intent(ChoosingABusiness.this,Login.class);
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


    public void setUid(View view) {

    }
}