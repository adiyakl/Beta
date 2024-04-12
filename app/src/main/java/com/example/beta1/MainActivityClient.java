package com.example.beta1;

import static com.example.beta1.DBref.mAuth;
import static com.example.beta1.DBref.refUsers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class MainActivityClient extends AppCompatActivity {

    TextView welcome ;
    String Sname;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_client);
        welcome = findViewById(R.id.welcomSign);



    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("stayConnect", true);
        editor.commit();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference currentUserRef = refUsers.child(uid);
            currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        user = dataSnapshot.getValue(User.class);
                        Sname = user.getName();
                        welcome.setText("hello "+Sname+" !");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle potential errors here
                }
            });
        }}


    public void pressed(View view) {

            mAuth.signOut();
            Intent intent = new Intent(MainActivityClient.this,Login.class);
            startActivity(intent);
    }
}