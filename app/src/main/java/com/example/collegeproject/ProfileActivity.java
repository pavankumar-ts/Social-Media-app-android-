package com.example.collegeproject;

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

import com.bumptech.glide.Glide;
import com.example.collegeproject.Model.UserProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference userDB;

    StorageReference storageRef;
    Button addImg, btnUplaod;
    EditText name, dob, bio;
    Uri imageUri;
    ImageView imageView;
    ProgressBar progressBar;
    Map<String, Object> userProfile = new HashMap<>();


    //current user id
    String Cuid = user.getUid();
    String tag;
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
        Intent intent=getIntent();
        tag = intent.getStringExtra("tag");
        Toast.makeText(this, "tag: "+tag, Toast.LENGTH_SHORT).show();
        Log.d("tag",tag);
        if (tag == "register"){
            userDB = FirebaseDatabase.getInstance().getReference().child("userProfile").child(Cuid);
            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);


                    if(userProfile.getUri() != null){
                        Glide.with(imageView.getContext()).load(userProfile.getUri()).into(imageView);
                        name.setText(userProfile.getName());
                        dob.setText(userProfile.getDob());
                        bio.setText(userProfile.getBio());
                        Log.d("not null",userProfile.getUri());
                    }
                    // ..
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.e("firebase", "Error getting data", databaseError.toException());
                }
            };
            userDB.addValueEventListener(postListener);
        }




        addImg.setOnClickListener(v -> {
            selectImage();
        });
        btnUplaod.setOnClickListener(v -> {
            profileUpload();
        });
    }

    private void profileUpload() {

        //read name, bio, DOB
        String txtName = name.getText().toString();
        String txtBio = bio.getText().toString();
        String txtDOB = dob.getText().toString();
        if (TextUtils.isEmpty(txtName) || TextUtils.isEmpty(txtBio) ||
                TextUtils.isEmpty(txtDOB) || imageUri == null) {
            Toast.makeText(getApplicationContext(), "fill the Fields and Select the image", Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            storageRef = FirebaseStorage.getInstance().getReference("profileImages/" + Cuid.toString());
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
                                    userProfile.put("userId", Cuid.toString());
                                    userProfile.put("bio", txtBio);
                                    userProfile.put("dob", txtDOB);
                                    reference.child(Cuid).setValue(userProfile);
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