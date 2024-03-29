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

import java.util.Calendar;
import java.util.Locale;

public class StartActivity extends AppCompatActivity {
    Button btnRegister;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        btnLogin = findViewById(R.id.btnLoginNav);
        btnRegister = findViewById(R.id.BtnRegisterNav);


        //time
        Long timeStamp = System.currentTimeMillis();

        String amPm = DateFormat.format("aa", timeStamp).toString().toLowerCase();
        String hourString = (String) DateFormat.format("hh", timeStamp);
        int hours = Integer.parseInt(hourString);

//        Log.d(TAG, "hours:" + amPm);
//        if (amPm.equals("am")){
//            startActivity(new Intent(StartActivity.this, DenialActivity.class));
//            Log.d(TAG, "hoursStart" + hours);
//        }
//        if (amPm.equals("pm")) {
//            Log.d(TAG, "amPm" + amPm);
//            if (hours < 6 || hours >= 9) {
//                Log.d(TAG, "hoursPm" + hours);
//                startActivity(new Intent(StartActivity.this, DenialActivity.class));
//                finish();
//            }
//        }


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