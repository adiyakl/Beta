 package com.example.beta1;

import static com.example.beta1.DBref.mAuth;
import static com.example.beta1.DBref.refUsers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

 public class MainActivity extends AppCompatActivity {
     CheckBox cB2;
     TextView name1 , email1, phone1;
     String Sname , Semail, Sphone;
     User user;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);
         cB2 = findViewById(R.id.cB2);
         name1 = findViewById(R.id.name1);
         email1 = findViewById(R.id.bname);
         phone1 = findViewById(R.id.phone1);


     }

     @Override
     protected void onStart() {
         super.onStart();

         SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
         Boolean isChecked=settings.getBoolean("stayConnect",false);
         cB2.setChecked(isChecked);
         // Assuming you have a DatabaseReference object for the user's data
         // Attach a ValueEventListener to read the data

         FirebaseUser currentUser = mAuth.getCurrentUser();

         if (currentUser != null) {
             String uid = currentUser.getUid();

             // Assuming you have a DatabaseReference object for the user's data
             DatabaseReference currentUserRef = refUsers.child(uid);

             currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     // Check if the snapshot exists
                     if (dataSnapshot.exists()) {
                         // Get the user object
                         user = dataSnapshot.getValue(User.class);
                         // Access the phone and password fields
                         Sphone = user.getPhone();
                         Sname = user.getName();
                         Semail = user.getEmail();

                         phone1.setText(Sphone);
                         name1.setText(Sname);
                         email1.setText(Semail);
                     }
                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {
                     // Handle potential errors here
                 }
             });
         }}


     public void pressed(View view) {
         if (!cB2.isChecked()){
             mAuth.signOut();
         }
         SharedPreferences settings =getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
         SharedPreferences.Editor editor=settings.edit();
         editor.putBoolean("stayConnect",cB2.isChecked());
         editor.commit();
         finish();
     }
}