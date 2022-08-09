package com.example.collegeproject.ui;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.collegeproject.DashboardActivity;
import com.example.collegeproject.DenialActivity;
import com.example.collegeproject.Model.UserProfile;
import com.example.collegeproject.R;
import com.example.collegeproject.databinding.FragmentPostBinding;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class PostFragment extends Fragment {

    private FragmentPostBinding binding;
    //Db ref
    private DatabaseReference usersDatabase;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    StorageReference storageRef;
    private DatabaseReference userProfileDB;

    UserProfile userData;

    Button btnAddImage, btnPost, btnAddVideo, playBtn;
    EditText desc, loc;
    ImageView imageView;
    VideoView videoView;
    Uri imageUri, videoUri, uploadUri;
    ProgressBar progressBar;
    String checkUri;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPostBinding.inflate(inflater, container, false);

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
            if (hours < 6 || hours >= 9) {
                Log.d(TAG, "hours" + hours);
                startActivity(new Intent(getActivity(), DenialActivity.class));
                getActivity().finish();
            }
        }


        View root = binding.getRoot();
        btnAddImage = binding.btnAddImage;
        btnPost = binding.btnPost;
        btnAddVideo = binding.btnAddVideo;
        imageView = binding.imageViewPost;
        videoView = binding.videoView;
        progressBar = binding.progressBarPost;
        desc = binding.desc;
        loc = binding.loc;
        btnAddImage.setOnClickListener(v -> {
            selectImage();
        });
        userProfileDB = FirebaseDatabase.getInstance().getReference().child("userProfile").child(user.getUid());
        //adding data into Views like TextView, imageView
        ValueEventListener profileListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userData = snapshot.getValue(UserProfile.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        btnPost.setOnClickListener(v -> {
            String txtDesc = desc.getText().toString();
            String txtLoc = loc.getText().toString();
            if (TextUtils.isEmpty(txtDesc) || TextUtils.isEmpty(txtLoc)) {
                Toast.makeText(getContext(), "fill the Fields", Toast.LENGTH_SHORT).show();
            }
            else if (imageUri == null && videoUri == null ){
                Toast.makeText(getContext(), "Select the Image/Video", Toast.LENGTH_SHORT).show();
            }
            else {
                uplaod(txtDesc, txtLoc);
            }
        });
        btnAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectVideo();
            }
        });

        return root;
    }

    private void selectVideo() {
        Intent i = new Intent();
        i.setType("video/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, 10);
    }

    //image
    private void selectImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, 100);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null && data.getData() != null) {
            imageUri = data.getData();
            Log.d(TAG, "imageUri" + imageUri);
            imageView.setImageURI(imageUri);

        }
        if (requestCode == 10 && data != null && data.getData() != null) {
            videoUri = data.getData();
            Log.d(TAG, "VideoUri" + videoUri);
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoURI(videoUri);
            ///.

            MediaController mediaController = new MediaController(getContext());

            // sets the anchor view
            // anchor view for the videoView
            mediaController.setAnchorView(videoView);

            // sets the media player to the videoView
            mediaController.setMediaPlayer(videoView);

            // sets the media controller to the videoView
            videoView.setMediaController(mediaController);

            // starts the video
            videoView.start();
        }
    }

    private void uplaod(String txtDesc, String txtLoc) {

        progressBar.setVisibility(View.VISIBLE);
        storageRef = FirebaseStorage.getInstance().getReference("postImages/" + UUID.randomUUID().toString());
        if (imageUri != null) {
            uploadUri = imageUri;
            checkUri = "imageUri";
        } else {
            uploadUri = videoUri;
            checkUri = "videoUri";
        }
        storageRef.putFile(uploadUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final String timestamp = String.valueOf(System.currentTimeMillis());
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String Cuid = user.getUid();
                                Map<Object, String> post = new HashMap<>();
                                DatabaseReference reference = database.getReference("posts");
                                //creating unique postId
                                String postId = reference.push().getKey();
                                if (checkUri == "imageUri") {
                                    post.put("imageUrl", uri.toString());
                                } else {
                                    post.put("videoUrl", uri.toString());
                                }
                                post.put("postId", postId);
                                post.put("timeStamp", timestamp);
                                post.put("userId", Cuid);
                                post.put("likes", "0");
                                post.put("descriptionText", txtDesc);
                                post.put("location", txtLoc);
                                reference.child(postId).setValue(post);
                                startActivity(new Intent(getContext(), DashboardActivity.class));
                                getActivity().finish();
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getContext(), "Successfully uploaded", Toast.LENGTH_SHORT).show();
                                imageView.setImageResource(R.drawable.ic_baseline_add_to_photos_24);
                                desc.setText(null);
                                loc.setText(null);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}