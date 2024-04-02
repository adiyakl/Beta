package com.example.beta1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import static com.example.beta1.DBref.mAuth;
import static com.example.beta1.DBref.refActiveBusiness;

import static com.example.beta1.DBref.refOffBusiness;
import static com.example.beta1.DBref.refUsers;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BusinessEditing extends AppCompatActivity {
    private EditText ename,eadress,ephone,eservices;
    private Switch sactive;
    String name, adress, phone,active, services;
    Button update;
    private String uid;
    Business business;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_editing);
        ename = findViewById(R.id.bname);
        eadress = findViewById(R.id.badress);
        ephone = findViewById(R.id.bphone);
        eservices = findViewById(R.id.bservices);
        sactive = findViewById(R.id.active);
        update = findViewById(R.id.button1);
        sactive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!sactive.isChecked()) {
                    sactive.setText("Inactive business");
                }
                else {
                    sactive.setText("active business");
                }
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
            Toast.makeText(BusinessEditing.this,"current user is null",Toast.LENGTH_SHORT).show();
        }
        showData();
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                name = String.valueOf(ename.getText());
                adress = String.valueOf(eadress.getText());
                phone = String.valueOf(ephone.getText());
                services = String.valueOf(eservices.getText());

                // Check if fields are null
                if(name == null || name.isEmpty()) {
                    Toast.makeText(BusinessEditing.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(adress == null || adress.isEmpty()) {
                    Toast.makeText(BusinessEditing.this, "Please enter an address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(phone == null || phone.isEmpty()) {
                    Toast.makeText(BusinessEditing.this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(services == null || services.isEmpty()) {
                    services = "This business has no service description";
                }

                if(sactive.isChecked()){
                    active = "True";
                }
                else {
                    active = "False";
                }


                business = new Business(name, adress, phone,active,services, uid);
                // Check if the business is active
                if (business.getActive().equals("True")) {
                    // Write data to the active business tree
                    refActiveBusiness.child(uid).setValue(business);
                    // Check if the business also exists in the off business tree
                    refOffBusiness.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Remove the business from the off business tree
                                refOffBusiness.child(uid).removeValue();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle onCancelled event
                        }
                    });

                } else {
                    // Write data to the off business tree
                    refOffBusiness.child(uid).setValue(business);

                    // Check if the business also exists in the active business tree
                    refActiveBusiness.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Remove the business from the active business tree
                                refActiveBusiness.child(uid).removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle onCancelled event
                        }
                    });
                }
                showData();

            }
        });

    }
    public void showData(){
        // Fetch business data from Firebase and populate EditText fields
        refActiveBusiness.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Business data exists, populate EditText fields
                    Business existingBusiness = dataSnapshot.getValue(Business.class);
                    ename.setText(existingBusiness.getName());
                    eadress.setText(existingBusiness.getAdress());
                    ephone.setText(existingBusiness.getPhone());
                    eservices.setText(existingBusiness.getServices());
                    sactive.setChecked(existingBusiness.getActive().equals("True"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event
            }
        });
        return;
    }

}