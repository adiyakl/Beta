package com.example.beta1;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    EditText eTemail, eTpass;
    Button bt1;
    CheckBox stayco;
    boolean stayCon;
    String email1, password1;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        eTemail = findViewById(R.id.email1);
        eTpass = findViewById(R.id.password);
        stayco = findViewById(R.id.checkBox);
        bt1 = findViewById(R.id.button1);
        mAuth = FirebaseAuth.getInstance();
        stayCon = false;
    }


    public void log(View view) {
        email1 = eTemail.getText().toString();
        password1 = eTpass.getText().toString();
        mAuth.signInWithEmailAndPassword(email1, password1)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
                            SharedPreferences.Editor editor=settings.edit();
                            editor.putBoolean("stayConnect",stayco.isChecked());
                            editor.commit();
                            Toast.makeText(Login.this, "Login Success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this,MainActivity.class);
                            startActivity(intent);
                            finish();
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
        Intent si = new Intent(Login.this, MainActivity.class);
        if (isChecked && mAuth.getCurrentUser() != null) {
            si.putExtra("newuser", false);
            startActivity(si);
            finish();
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