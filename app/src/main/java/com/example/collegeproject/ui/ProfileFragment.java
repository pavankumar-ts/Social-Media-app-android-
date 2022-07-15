package com.example.collegeproject.ui;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collegeproject.Model.ModelPost;
import com.example.collegeproject.Model.UserProfile;
import com.example.collegeproject.ProfileActivity;
import com.example.collegeproject.R;
import com.example.collegeproject.StartActivity;
import com.example.collegeproject.adapter.PostDisplayAdapter;
import com.example.collegeproject.databinding.FragmentProfileBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    //DB
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    // ...auth
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    TextView name, dob, bio;
    Button btnEdit;
    ImageView dp;
    LinearLayout logout;
    RecyclerView recyclerView;
    PostDisplayAdapter adapter;
    LinearLayout saved, disp4Cuser;

    String Cuid = user.getUid();
    String fragment, userId;

    public ProfileFragment() {
        userId = Cuid;
    }

    public ProfileFragment(String fragment, String userId) {
        this.fragment = fragment;
        this.userId = userId;
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        name = binding.profileName;
        dp = binding.profileDp;
        dob = binding.profileDob;
        btnEdit = binding.btnEditProfile;
        bio = binding.profileBio;
        saved = binding.saved;
        logout = binding.logout;
        disp4Cuser = binding.disp4Cuser;

        //ref
        DatabaseReference userDbRef = database.getReference().child("userProfile").child(userId);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userData = dataSnapshot.getValue(UserProfile.class);
                // ..
                name.setText("Name : " + userData.getName());
                bio.setText(userData.getBio());
                dob.setText("DOB : " + userData.getDob());
                Glide.with(dp.getContext()).load(userData.getUri()).into(dp);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        userDbRef.addValueEventListener(postListener);

        recyclerView = binding.rvProfile;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        FirebaseRecyclerOptions<ModelPost> options =
                new FirebaseRecyclerOptions.Builder<ModelPost>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("userId").equalTo(userId), ModelPost.class)
                        .build();

        adapter = new PostDisplayAdapter(options, "ProfileFragment");
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);
        adapter.notifyDataSetChanged();

        Log.d("userId.....x.x.", userId);
        Log.d("userId.....x.x.", Cuid);


        //disp4Cuser
        if (userId.equals(Cuid)) {
            disp4Cuser.setVisibility(View.VISIBLE);
            Log.d(TAG, "EQUAL");
        }else {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) disp4Cuser.getLayoutParams();
            layoutParams.height = 0;
            disp4Cuser.setLayoutParams(layoutParams);
        }
        //saved
        saved.setOnClickListener(v -> {
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.Fprofile, new SavedFragment())
                    .addToBackStack(null)
                    .commit();
        });

        //btnEdit
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);

        });


        //logout
        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), StartActivity.class));
        });


        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}