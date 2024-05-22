package com.example.beta1;

import static com.example.beta1.CalendarUtils.Ddate;
import static com.example.beta1.CalendarUtils.Odate;
import static com.example.beta1.CalendarUtils.Sdate;
import static com.example.beta1.CalendarUtils.selectedDate;

import static com.example.beta1.DBref.refActiveAppointments;
import static com.example.beta1.DBref.refPic;
import static com.example.beta1.MainActivityClient.Muid;
import static com.example.beta1.MainActivityClient.thisbusiness;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;

public class AppointmentSet extends AppCompatActivity {
    private TextView dateAndDay, bName;
    private EditText req;
    private String sdate = " ";
    private String time = " ";
    private ImageView iv;
    private String CorG;
    private CheckBox cb;
    private String  notes, wwKey, date;
    private String currentPath;
    private StorageReference refSto;
    private static Uri file;
    private LocalDate localDate;
    private LocalTime Ttime;
    private LocalDateTime localDateTime;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private int ALARM_RQST_CODE=0;


    private static final int REQUEST_CAMERA_PERMISSION = 1;//id the permission
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 3;
    private static final int REQUEST_PICK_IMAGE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_set);
        dateAndDay = findViewById(R.id.Atime);
        bName = (findViewById(R.id.ClientName));
        req = findViewById(R.id.notes);
        cb = findViewById(R.id.aCb);
        iv = findViewById(R.id.image);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sdate = extras.getString("date");
            time = extras.getString("hour");
            wwKey = extras.getString("windowKey");
        }
        dateAndDay.setText(Odate(sdate) + " at " + time);
        bName.setText(thisbusiness.getName());

        // Request camera and storage permissions if not granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
        }

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            localDate = Ddate(sdate);
            Ttime =CalendarUtils.Ttime(time);
            localDateTime = LocalDate.now().atTime(LocalTime.now().plusMinutes(1));
                 localDateTime = localDate.atTime(Ttime.minusMinutes(7));// 30 minus...

            }
        });
    }
    public void setApp(View view) {
        if(cb.isChecked()){
            ALARM_RQST_CODE++;
            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("msg", String.valueOf(ALARM_RQST_CODE) + " TOD");
            alarmIntent = PendingIntent.getBroadcast(this,
                    ALARM_RQST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
            alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            long alarmTimeMillis = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//             Set the alarm
            alarmMgr.set(AlarmManager.RTC_WAKEUP, alarmTimeMillis, alarmIntent);
        Toast.makeText(AppointmentSet.this,String.valueOf(ALARM_RQST_CODE) + " Alarm in " + String.valueOf(localDateTime.getHour())+"at "+ String.valueOf(localDateTime.getMinute()),Toast.LENGTH_LONG).show();
        }
        if (CorG!=null) {
            if (CorG.equals("G")) {
                uplodegal();
            }
            if (CorG.equals("C")) {
                uplodecam();
            }
        }
        notes = String.valueOf(req.getText());
        Appointment appointment = new Appointment(time, sdate, DBref.user.getName(), DBref.uid, Muid, notes,DBref.user.getPhone());
        refActiveAppointments.child(Muid).child(sdate).child(wwKey).child(time).setValue(appointment);
        Intent intent = new Intent(this, MainActivityClient.class);
        startActivity(intent);
    }



    // Handle permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE_PERMISSION) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Handle activity result (image capture or image pick from gallery)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data_back) {
        super.onActivityResult(requestCode, resultCode, data_back);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(currentPath);
            CorG = "C";
            iv.setImageBitmap(imageBitmap);
        }

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data_back != null) {
                CorG = "G";
                file = data_back.getData();
                iv.setImageURI(file);
            }
            else {
                Toast.makeText(this, "No Image was selected", Toast.LENGTH_LONG).show();
            }

        }
    }

    private void uplodecam(){
        final ProgressDialog pd = ProgressDialog.show(this, "Upload image", "Uploading...", true);
        date = Sdate(selectedDate);
        refSto = refPic.child(Muid).child(date).child(date+time + ".jpg");
        Bitmap imageBitmap = BitmapFactory.decodeFile(currentPath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if(imageBitmap!=null)
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        refSto.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
//                        Toast.makeText(AppointmentSet.this, "Image Uploaded", Toast.LENGTH_LONG).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        Toast.makeText(AppointmentSet.this, "Upload failed", Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void uplodegal() {
        final ProgressDialog pd = ProgressDialog.show(this, "Upload image", "Uploading...", true);
        refSto = refPic.child(Muid).child(sdate).child(sdate + time + ".jpg");
        refSto.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
//                        Toast.makeText(AppointmentSet.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        Toast.makeText(AppointmentSet.this, "Upload failed", Toast.LENGTH_SHORT).show();
                    }
                });


}

//    open camera
    public void camera(View view) {
        String filename = "tempfile";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File imgFile = File.createTempFile(filename,".jpg",storageDir);
            currentPath = imgFile.getAbsolutePath();
            Uri imageUri = FileProvider.getUriForFile(AppointmentSet.this,"com.example.beta1.fileprovider",imgFile);
            Intent takePicIntent = new Intent();
            takePicIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            takePicIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,imageUri);
            if (takePicIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePicIntent, REQUEST_IMAGE_CAPTURE );
            }
        } catch (IOException e) {
            Toast.makeText(AppointmentSet.this,"Failed to create temporary file",Toast.LENGTH_LONG);
            throw new RuntimeException(e);
        }
    }






//  open gallery
    public void gallery(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);

    }
}