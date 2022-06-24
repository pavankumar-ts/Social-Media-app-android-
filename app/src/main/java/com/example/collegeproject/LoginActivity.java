package com.example.collegeproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    EditText userName;
    EditText password;
    Button btnLogin;
    TextView registerNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userName = findViewById(R.id.LuserName);
        password = findViewById(R.id.Lpassword);
        btnLogin = findViewById(R.id.btnLogin);
        registerNav = findViewById(R.id.registerNav);
        registerNav.setOnClickListener(v ->{
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }
}