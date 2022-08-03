package com.example.collegeproject;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity<mAuth> extends AppCompatActivity {
    EditText phoneNo, email, password;
    Button btnRegister, btnVerify;
    TextView loginNav;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    String txtPhoneNo, txtEmail, txtPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_register);
        btnRegister = findViewById(R.id.btnRegister);
        btnVerify = findViewById(R.id.btnVerify);
        phoneNo = findViewById(R.id.phoneNo);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBarReg);
        loginNav = findViewById(R.id.loginNav);
        loginNav.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                txtPhoneNo = phoneNo.getText().toString();
                txtEmail = email.getText().toString().trim();
                txtPassword = password.getText().toString();
                if (TextUtils.isEmpty(txtPhoneNo) || TextUtils.isEmpty(txtEmail) ||
                        TextUtils.isEmpty(txtPassword)) {
                    Toast.makeText(getApplicationContext(), "fill the Fields", Toast.LENGTH_SHORT).show();
                } else if (txtPassword.length() < 6) {
                    Toast.makeText(getApplicationContext(), "short password", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(txtEmail, txtPassword);
                }
            }
        });
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user =  mAuth.getCurrentUser();
                user.reload();
                Log.d("veried?", ""+mAuth.getCurrentUser().isEmailVerified());
                if (mAuth.getCurrentUser().isEmailVerified()) {
                    progressBar.setVisibility(View.VISIBLE);
                    addDataToDb(txtEmail, txtPhoneNo, txtPassword);
                } else {
                    Toast.makeText(RegisterActivity.this, "Verification link sent to registered Email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void registerUser(String txtEmail, String txtPassword) {
        mAuth.createUserWithEmailAndPassword(txtEmail, txtPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            btnVerify.setVisibility(View.VISIBLE);
                            btnRegister.setVisibility(View.GONE);
                            progressBar.setVisibility(View.INVISIBLE);
                            verify();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed\n E-mail is already in use\n or \n invalid E-mail",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addDataToDb(String email, String txtPhoneNo, String txtPassword) {
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("phoneNo", txtPhoneNo);
        userData.put("password", txtPassword);
        userData.put("userId", uid);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users");
        reference.child(uid).setValue(userData);
        // Sign in success, update UI with the signed-in user's information
        Log.d(TAG, "createUserWithEmail:success" + user);
        Toast.makeText(RegisterActivity.this, "Registered User " + user.getEmail(), Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(RegisterActivity.this, ProfileActivity.class);
        intent.putExtra("tag", "register");
        startActivity(intent);
        finish();
    }

    private void verify() {
        progressBar.setVisibility(View.INVISIBLE);
        mAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(RegisterActivity.this, "Verification link sent to registered Email", Toast.LENGTH_SHORT).show();
            }
        });
    }
}