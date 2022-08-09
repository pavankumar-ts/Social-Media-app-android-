package com.example.collegeproject;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.protobuf.Timestamp;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class StartActivity extends AppCompatActivity {
    Button btnRegister;
    Button btnLogin;
    DatabaseReference DatabaseInstance = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        btnLogin = findViewById(R.id.btnLoginNav);
        btnRegister = findViewById(R.id.BtnRegisterNav);


        //time
        Long timeStamp = System.currentTimeMillis();
        Calendar time = Calendar.getInstance(Locale.ENGLISH);
        time.setTimeInMillis(Long.parseLong(String.valueOf(timeStamp)));

        String amPm = DateFormat.format("aa", timeStamp).toString().toLowerCase();
        String hourString = (String) DateFormat.format("hh", timeStamp);
        int hours = Integer.parseInt(hourString);

        Log.d(TAG, "hours" + hours);
        if (amPm.equals("pm")) {
            Log.d(TAG, "amPm" + amPm);
            if (hours <  6 || hours >= 9) {
                Log.d(TAG, "hours" + hours);
                startActivity(new Intent(StartActivity.this, DenialActivity.class));
                finish();
            }
        }


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(new Intent(StartActivity.this, DashboardActivity.class));
            finish();
        } else {
            // No user is signed in
            btnRegister.setOnClickListener(v -> {
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));

            });
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(StartActivity.this, LoginActivity.class));
                }
            });
        }
    }

}