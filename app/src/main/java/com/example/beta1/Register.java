package com.example.beta1;

import static com.example.beta1.DBref.refUsers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends AppCompatActivity {
    private FirebaseAuth mAuth;
    TextView toLogin;
    CheckBox stayco;
    TextView manicurist;
    TextView client;
    String mOrC;
    User userdb;
    EditText eTname, eTphone, eTemail, eTpass;
    Button bt1;
    boolean stayCon;
    String email, password, uid, name, phone;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        toLogin = findViewById(R.id.toLogin);
        manicurist = findViewById(R.id.mani);
        client = findViewById(R.id.client);
        mOrC = "C";
         eTname = findViewById(R.id.name);
         eTemail = findViewById(R.id.email1);
         eTpass = findViewById(R.id.password);
         eTphone = findViewById(R.id.phone);
         bt1 = findViewById(R.id.button1);
         stayco = findViewById(R.id.checkBox);
         mAuth = FirebaseAuth.getInstance();

         SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
         SharedPreferences.Editor editor=settings.edit();
         editor.putBoolean("stayConnect",stayco.isChecked());
         editor.commit();
         stayCon = true;

     }
    public void reg(View view) {
        email = String.valueOf(eTemail.getText());
        password = String.valueOf(eTpass.getText());
        name=eTname.getText().toString();
        phone=eTphone.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            uid = user.getUid();
                            userdb=new User(name,email,phone,uid,mOrC);
                            refUsers.child(uid).setValue(userdb);
                            if(userdb.getmOrC()=="M"){
                                Intent intent = new Intent(Register.this,BusinessEditing.class);
                                startActivity(intent);
                            }
                            else {//to c
                                Intent intent = new Intent(Register.this,MainActivity.class);
                                startActivity(intent);
                            }

                            finish();
                        } else {
                            Log.d("abcde","reg doean't work");
                            Toast.makeText(Register.this, "reg Failed", Toast.LENGTH_SHORT).show();
                        }

                    }               });
    }

    public void goToLogin(View view){
        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
    }

    public void clientSel(View view){
        mOrC = "C";
        Toast.makeText(Register.this,mOrC, Toast.LENGTH_SHORT).show();

    }

    public void manicuristSel(View view) {
        mOrC = "M";
        Toast.makeText(Register.this,mOrC, Toast.LENGTH_SHORT).show();
    }



}