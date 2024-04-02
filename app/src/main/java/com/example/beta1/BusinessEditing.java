package com.example.beta1;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class BusinessEditing extends AppCompatActivity {
    private EditText name,adress,phone;
    private Switch active;
    private ArrayList<Service> services;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_editing);
        name = findViewById(R.id.bname);
        adress = findViewById(R.id.badress);
        phone = findViewById(R.id.bphone);
        active = findViewById(R.id.active);


    }
}