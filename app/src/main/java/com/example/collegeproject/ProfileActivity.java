package com.example.collegeproject;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    StorageReference storageRef;
    Button addImg, btnUplaod;
    EditText name, dob, bio;
    Uri imageUri;
    ImageView imageView;
    ProgressBar progressBar;
    Map<String, Object> userProfile = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        addImg = findViewById(R.id.btnAddImg);
        name = findViewById(R.id.Pname);
        bio = findViewById(R.id.bio);
        dob = findViewById(R.id.dob);
        btnUplaod = findViewById(R.id.btnProfileUplaod);
        addImg.setOnClickListener(v -> {
            selectImage();
        });
        btnUplaod.setOnClickListener(v -> {
            profileUpload();
        });
    }

    private void profileUpload() {
        String uid = user.getUid();
        //read name, bio, DOB
        String txtName = name.getText().toString();
        String txtBio = bio.getText().toString();
        String txtDOB = dob.getText().toString();
        if (TextUtils.isEmpty(txtName) || TextUtils.isEmpty(txtBio) ||
                TextUtils.isEmpty(txtDOB) || imageUri == null) {
            Toast.makeText(getApplicationContext(), "fill the Fields and Select the image", Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            storageRef = FirebaseStorage.getInstance().getReference("profileImages/" + uid.toString());
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    userProfile.put("uri", uri.toString());
                                    DatabaseReference reference = database.getReference("userProfile");
                                    userProfile.put("name", txtName);
                                    userProfile.put("userId", uid.toString());
                                    userProfile.put("bio", txtBio);
                                    userProfile.put("dob", txtDOB);
                                    reference.child(uid).setValue(userProfile);
                                    Toast.makeText(getApplicationContext(), "Successfully uploaded", Toast.LENGTH_SHORT).show();
                                    imageView.setImageURI(null);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    startActivity(new Intent(ProfileActivity.this, DashboardActivity.class));
                                    finish();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }


    private void selectImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }
}