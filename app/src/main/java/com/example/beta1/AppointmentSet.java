package com.example.beta1;

import static com.example.beta1.ChangeType.Odate;
import static com.example.beta1.DBref.refActiveAppointments;
import static com.example.beta1.MainActivityClient.Cuid;
import static com.example.beta1.MainActivityClient.Muid;
import static com.example.beta1.MainActivityClient.thisUser;
import static com.example.beta1.MainActivityClient.thisbusiness;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AppointmentSet extends AppCompatActivity {
    TextView dateAndDay,bName;
    EditText req;
    private String sdate = " ";
    private String time=" ";
    private String Bname =thisbusiness.getName();
    private ImageView iv;
    private String imageUri, notes, wwKey;



    private static final int REQUEST_CAMERA_PERMISSION = 1;//id the permission
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 3;
    private static final int REQUEST_PICK_IMAGE = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_set);
        dateAndDay = findViewById(R.id.Atime);
        bName = (findViewById(R.id.businName));
        req = findViewById(R.id.notes);
        iv = findViewById(R.id.image);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
             sdate = extras.getString("date");
             time = extras.getString("hour");
             wwKey = extras.getString("windowKey");
        }
        dateAndDay.setText(Odate(sdate)+" at "+time);
        bName.setText(thisbusiness.getName());

        // Request camera and storage permissions if not granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
        }
    }


    public void setApp(View view) {
        imageUri = getImageUriString();
        notes = String.valueOf(req.getText());
        Appointment appointment =new Appointment(sdate,time,thisUser.getName(),Cuid,Muid,notes,"HAPPENING",imageUri);
        refActiveAppointments.child(Muid).child(sdate).child(wwKey).child(time).setValue(appointment);
        Intent intent = new Intent(this,MainActivityClient.class);
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
            Bundle extras = data_back.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                iv.setImageBitmap(imageBitmap);
            }
        }
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data_back != null) {
                Uri imageUri = data_back.getData();
                iv.setImageURI(imageUri);
            }
        }
    }
    // Method to get the URI of the image
    private String getImageUriString() {
        // Get the URI of the image from ImageView
        if (iv.getDrawable() instanceof BitmapDrawable) {
            BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            // Convert Bitmap to URI
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Image", null);
            if (path != null) {
                return path;
            } else {
                return "";
            }
        }
        return "";
    }
//    open camera
    public void camera(View view) {
        Intent takePicIntent = new Intent();
        takePicIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePicIntent, REQUEST_IMAGE_CAPTURE);
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