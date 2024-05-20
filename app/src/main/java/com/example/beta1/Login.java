package com.example.beta1;

import static com.example.beta1.DBref.FBDB;
import static com.example.beta1.DBref.mAuth;
import static com.example.beta1.DBref.refUsers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
    private User user = new User("","","","","","","");
    String email1, password1;
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
        if (email1.isEmpty() || password1.isEmpty()) {
            Toast.makeText(Login.this, "please enter fileds<3", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!email1.contains("@")||!email1.contains(".com")){
            Toast.makeText(Login.this,"email address must contain @ and .com<3", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password1.length()<6){
            Toast.makeText(Login.this,"password must be at list 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email1, password1)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final ProgressDialog pd = ProgressDialog.show(Login.this, "login", "connecting...", true);
                            SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("stayConnect", stayco.isChecked());
                            editor.commit();
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                goToMC(currentUser);
                            }
                            else {
                                pd.dismiss();
                            }
                        } else {
                            Toast.makeText(Login.this, "Login Failed, check your fields or consider register...", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
    public void goToMC(FirebaseUser currentUser){
        DBref.getUserUid(currentUser);
        final ProgressDialog pd = ProgressDialog.show(Login.this, "Login", "Connecting...", true);
        refUsers.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    pd.dismiss();
                    User user = task.getResult().getValue(User.class);
                    DBref.user = user;
                    if (user.getmOrC().equals("M")) {
                        Intent si = new Intent(Login.this, MainActivityManicurist.class);
                        startActivity(si);
                        finish();

                    } else if (user.getmOrC().equals("C")) {
                        Intent si = new Intent(Login.this, MainActivityClient.class);
                        startActivity(si);
                        finish();
                    } else {
                        Toast.makeText(Login.this, user.getmOrC() ,Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
    }


                    protected void onStart() {
                        super.onStart();
                        SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
                        Boolean isChecked = settings.getBoolean("stayConnect", false);
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (isChecked && currentUser != null) {
                            goToMC(currentUser);
                    }}

                    protected void onPause() {
                        super.onPause();
                        if (stayCon) finish();
                    }

                    public void goToRegister(View view) {
                        Intent intent = new Intent(Login.this, Register.class);
                        startActivity(intent);
                    }



}