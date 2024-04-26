package com.example.beta1;

import static com.example.beta1.CalendarUtils.selectedDate;
import static com.example.beta1.ChangeType.Odate;
import static com.example.beta1.ChangeType.Sdate;
import static com.example.beta1.DBref.refActiveAppointments;
import static com.example.beta1.DBref.refPic;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class AppDetails extends AppCompatActivity {
    private String uid = DBref.uid;
    private String time, wwkey,sdate;
    private StorageReference refIm;
    private File localFile;
    private Appointment app = new Appointment("","","","","","");
    TextView dateAndHour, Cname, req;
    ImageView im;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_details);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
             time = extras.getString("time");
            Toast.makeText(AppDetails.this,time,Toast.LENGTH_SHORT).show();
            wwkey = extras.getString("windowKey");
        }
        im = findViewById(R.id.image);
        dateAndHour = findViewById(R.id.Atime);
        Cname = findViewById(R.id.ClientName);
        req = findViewById(R.id.notes);
        sdate =Sdate(selectedDate);
        dateAndHour.setText(Odate(sdate) + " at " + time);
        getApp();
        getIm();
    }
    public void getApp(){
        final ProgressDialog pd = ProgressDialog.show(this, "Upload image", "Uploading...", true);
        DatabaseReference currentDateRef = refActiveAppointments.child(uid).child(sdate).child(wwkey).child(time);
        currentDateRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()&& task.getResult().exists()) {
                    app = task.getResult().getValue(Appointment.class);
                    if(app!=null) {
                        Cname.setText(app.getName());
                        req.setText(app.getRequests());
                    }
                    pd.dismiss();
                }
                else {
                    pd.dismiss();
                    Toast.makeText(AppDetails.this,"app not found",Toast.LENGTH_SHORT).show();
                }
            }
         });

        }
        public void getIm(){
            final ProgressDialog pd = ProgressDialog.show(this, "Upload image", "Uploading...", true);
            refIm = refPic.child(uid).child(sdate+time+".jpg");
            try {
                localFile = File.createTempFile(sdate+time,"jpg");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            refIm.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    pd.dismiss();
                    Toast.makeText(AppDetails.this, "Image download success", Toast.LENGTH_LONG).show();
                    String filePath = localFile.getPath();
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    im.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    pd.dismiss();
                    Toast.makeText(AppDetails.this, "no image added", Toast.LENGTH_LONG).show();
                }
            });
        }
    public void close(View view) {
        finish();
    }
}