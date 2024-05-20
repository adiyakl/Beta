package com.example.beta1;

import static com.example.beta1.DBref.FBDB;
import static com.example.beta1.DBref.mAuth;
import static com.example.beta1.DBref.refActiveBusiness;
import static com.example.beta1.DBref.refActiveCalendar;
import static com.example.beta1.DBref.refUsers;


import android.app.ProgressDialog;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ChoosingABusiness extends AppCompatActivity {
    ListView list;
    AlertDialog.Builder logoutAlert;
    private ArrayList<String> businessesList = new ArrayList<>();
    private ArrayList<Business> businesses = new ArrayList<>();
    TextView busName,busServ,busAdres,busPhone,busMani;
    private Business b;
    private User user = DBref.user;
    private User Muser;
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
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(b!=null) {
                    setLinked1();
                    Toast.makeText(ChoosingABusiness.this,"your manicurist is now: "+b.getName(),Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(ChoosingABusiness.this,"please choose a business first",Toast.LENGTH_SHORT).show();
                }
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                b = businesses.get(i);
                linkUid = b.getUid();
                bringData(b);
            }
        });
        createList();
    }

            public void setLinked1(){
                user.setLinked(linkUid);
                DBref.setUser(user);
                refUsers.setValue(DBref.user);
                Toast.makeText(ChoosingABusiness.this,"updated",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ChoosingABusiness.this,MainActivityClient.class);
                startActivity(intent);
            }

            public void bringData(Business b) {
                busName.setText(b.getName());
                busPhone.setText(b.getPhone());
                busAdres.setText(b.getAdress());
                busServ.setText(b.getServices());
                DatabaseReference Muserref = refUsers.child(b.getUid());
                Muserref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            Muser = task.getResult().getValue(User.class);
                            if(Muser!=null)
                                busMani.setText(Muser.getName());
                        }
                    }
                });
                DBref.getUserUid(mAuth.getCurrentUser());
            }


    public void createList() {
        final ProgressDialog pd = ProgressDialog.show(ChoosingABusiness.this, "Business List", "Loading...", true);
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
                    pd.dismiss();
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
            finish();
        } else if (id == R.id.client_main) {
            Intent intent = new Intent(ChoosingABusiness.this,CalendarClient.class);
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
                    Intent intent = new Intent(ChoosingABusiness.this,Login.class);
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


    public void setUid(View view) {

    }
}