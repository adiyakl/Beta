package com.example.beta1;

import static com.example.beta1.DBref.refUsers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends AppCompatActivity {
     FirebaseAuth mAuth;
    TextView toLogin;
    CheckBox stayco;
    TextView manicurist;
    TextView client;
    String mOrC;
     User userdb;
    EditText eTname, eTphone, eTemail, eTpass;
    Button bt1;
    String email, password, name, phone;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        toLogin = findViewById(R.id.toLogin);
        manicurist = findViewById(R.id.mani);
        client = findViewById(R.id.client);
         eTname = findViewById(R.id.name);
         eTemail = findViewById(R.id.bname);
         eTpass = findViewById(R.id.password);
         eTphone = findViewById(R.id.phone);
         bt1 = findViewById(R.id.button1);
         stayco = findViewById(R.id.checkBox);
         mAuth = FirebaseAuth.getInstance();
         mOrC = "C";
         client.setTextColor(Color.BLACK);
         client.setBackgroundColor(Color.parseColor("#FFEFEF"));



     }
    public void reg(View view) {
        email = String.valueOf(eTemail.getText());
        password = String.valueOf(eTpass.getText());
        name=eTname.getText().toString();
        phone=eTphone.getText().toString();

        // Check if any field is empty
        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(Register.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!email.contains("@")||!email.contains(".com")){
            Toast.makeText(Register.this,"email address must contain @ and .com<3", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.length()<6){
            Toast.makeText(Register.this,"password must be at list 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if(phone.length()<10){
            Toast.makeText(Register.this, "phone number must be at list 10 characters",Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
        SharedPreferences.Editor editor=settings.edit();
        editor.putBoolean("stayConnect",stayco.isChecked());
        editor.commit();
        final ProgressDialog pd = ProgressDialog.show(Register.this, "Registering", "Connecting...", true);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            DBref.getUserUid(user);
                            userdb=new User(name,email,phone,DBref.uid,mOrC,password,"");
                            refUsers.setValue(userdb);
                            DBref.setUser(userdb);
                            if(userdb.getmOrC().equals("M")){
                                pd.dismiss();
                                Intent intent = new Intent(Register.this,BusinessEditing.class);
                                startActivity(intent);
                            }
                            else {//to c
                                pd.dismiss();
                                Intent intent = new Intent(Register.this,ChoosingABusiness.class);
                                startActivity(intent);
                            }
                            pd.dismiss();
                            finish();
                        } else {
                            Toast.makeText(Register.this, "please check your fields", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }

                    }               });
    }

    public void goToLogin(View view){
       finish();
    }

    public void clientSel(View view){
        mOrC = "C";
        client.setTextColor(Color.BLACK);
        manicurist.setTextColor(Color.parseColor("#686D76"));
        client.setBackgroundColor(Color.parseColor("#FFEFEF"));
        manicurist.setBackgroundColor(Color.parseColor("#F6F5F2"));
     }

    public void manicuristSel(View view) {
        mOrC = "M";
        manicurist.setTextColor(Color.BLACK);
        client.setTextColor(Color.parseColor("#686D76"));
        client.setBackgroundColor(Color.parseColor("#F6F5F2"));
        manicurist.setBackgroundColor(Color.parseColor("#FFEFEF"));
    }



}