package com.example.collegeproject.ui;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.collegeproject.DashboardActivity;
import com.example.collegeproject.ProfileActivity;
import com.example.collegeproject.R;
import com.example.collegeproject.databinding.FragmentPostBinding;
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

public class PostFragment extends Fragment {

    private FragmentPostBinding binding;
    //Db ref
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    StorageReference storageRef;

    Button btnAddImage, btnPost;
    EditText desc, loc;
    ImageView imageView;
    Uri imageUri;
    ProgressBar progressBar;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPostBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        btnAddImage = binding.btnAddImage;
        btnPost = binding.btnPost;
        imageView = binding.imageViewPost;
        progressBar = binding.progressBarPost;
        desc = binding.desc;
        loc = binding.loc;
        btnAddImage.setOnClickListener(v -> {
            selectImage();
        });
        btnPost.setOnClickListener(v -> {
            String txtDesc = desc.getText().toString();
            String txtLoc = loc.getText().toString();
            if (TextUtils.isEmpty(txtDesc) || TextUtils.isEmpty(txtLoc) || imageUri == null) {
                Toast.makeText(getContext(), "fill the Fields and Select the image", Toast.LENGTH_SHORT).show();
            }else {
                uplaod(txtDesc, txtLoc);
            }
        });
        return root;
    }

    private void selectImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,100);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null && data.getData() != null){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void uplaod(String txtDesc, String txtLoc) {
        progressBar.setVisibility(View.VISIBLE);
        storageRef = FirebaseStorage.getInstance().getReference("postImages/"  + UUID.randomUUID().toString());
        storageRef.putFile(imageUri)
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
                                post.put("imageUrl", uri.toString());
                                post.put("postId", postId);
                                post.put("timeStamp", timestamp);
                                post.put("userId", Cuid);
                                post.put("likes", "0");
                                post.put("descriptionText", txtDesc);
                                post.put("location", txtLoc);
                                reference.child(postId).setValue(post);
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
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}