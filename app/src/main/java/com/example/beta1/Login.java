package com.example.beta1;

import static com.example.beta1.DBref.mAuth;
import static com.example.beta1.DBref.refUsers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    EditText eTemail, eTpass;
    Button bt1;
    CheckBox stayco;
    boolean stayCon;
    DataSnapshot dataSnapshot;
    String email1, password1;
    User user;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        eTemail = findViewById(R.id.bname);
        eTpass = findViewById(R.id.password);
        stayco = findViewById(R.id.checkBox);
        bt1 = findViewById(R.id.button1);
        mAuth = FirebaseAuth.getInstance();
        stayCon = false;
    }


    public void log(View view) {
        email1 = eTemail.getText().toString();
        password1 = eTpass.getText().toString();
        if(email1.isEmpty()||password1.isEmpty()){
            Toast.makeText(Login.this,"please enter fileds<3",Toast.LENGTH_SHORT).show();
        }
        mAuth.signInWithEmailAndPassword(email1, password1)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
                            SharedPreferences.Editor editor=settings.edit();
                            editor.putBoolean("stayConnect",stayco.isChecked());
                            editor.commit();
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                String uid = currentUser.getUid();
                                DatabaseReference currentUserRef = refUsers.child(uid);
                                currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        // Check if the snapshot exists
                                        if (dataSnapshot.exists()) {
                                            // Get the user object
                                            user = dataSnapshot.getValue(User.class);
                                            // Access the phone and password fields
                                            Intent intent;
                                            if (user.getmOrC() == "M") {
                                                intent = new Intent(Login.this, MainActivityManicurist.class);
                                            } else {
                                                intent = new Intent(Login.this, MainActivityClient.class);
                                            }
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle potential errors here
                                    }
                                });
                            }

                        } else {
                            Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    protected void onStart() {
        super.onStart();
        SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        Boolean isChecked = settings.getBoolean("stayConnect", false);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference currentUserRef = refUsers.child(uid);
            currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        user = dataSnapshot.getValue(User.class);
                        if (user.getmOrC() == "M") {
                            Intent si = new Intent(Login.this, MainActivityManicurist.class);
                            if (isChecked && mAuth.getCurrentUser() != null) {
                                si.putExtra("newuser", false);
                                startActivity(si);
                                finish();
                            }
                        } else {
                            Intent si = new Intent(Login.this, MainActivityClient.class);
                            if (isChecked && mAuth.getCurrentUser() != null) {
                                si.putExtra("newuser", false);
                                startActivity(si);
                                finish();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle potential errors here
                }
            });
        }

    }
    protected void onPause() {
        super.onPause();
        if (stayCon) finish();
    }
    public void goToRegister(View view) {
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
    }



}