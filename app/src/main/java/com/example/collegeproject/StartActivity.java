package com.example.collegeproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {
    Button btnRegister;
    Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btnLogin = findViewById(R.id.btnLoginNav);
        btnRegister = findViewById(R.id.BtnRegisterNav);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(new Intent(StartActivity.this, DashboardActivity.class));
            finish();
        } else {
            // No user is signed in
            btnRegister.setOnClickListener(v -> {
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));
                finish();
            });
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(StartActivity.this, LoginActivity.class));
                    finish();
                }
            });
        }

    }
}