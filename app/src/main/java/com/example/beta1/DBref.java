package com.example.beta1;



import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DBref {
    public static String uid;
    public static User user ;
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();
    public static DatabaseReference refUsers = FBDB.getReference("Users");
    public static DatabaseReference refActiveBusiness = FBDB.getReference("Active Business");
    public static DatabaseReference refOffBusiness = FBDB.getReference("Inactive businesses");
    public static DatabaseReference refActiveCalendar = FBDB.getReference("Active calendars");
    public static DatabaseReference refActiveAppointments = FBDB.getReference("Appointments");
    public static FirebaseStorage FBST = FirebaseStorage.getInstance();
    public static StorageReference refPic = FBST.getReference();
    public static void getUserUid(FirebaseUser fbUser){
        uid = "";
    uid = fbUser.getUid();
    refUsers = FBDB.getReference("Users").child(uid);

          }
          public static void setUser(User user1){
            user = user1;
          }

}
