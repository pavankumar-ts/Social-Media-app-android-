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
import com.example.collegeproject.Model.Follow;
import com.example.collegeproject.Model.Post;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    //DB
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private Query followerDb, followingDb;
    // ...auth
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    TextView name, dob, bio, followingCount, followerCount;
    Button btnEdit, btnMsg, btnFollow;
    ImageView dp;
    LinearLayout logout;
    RecyclerView recyclerView;
    PostDisplayAdapter adapter;
    LinearLayout saved, disp4Cuser, nonCurrentUser, following, follower;

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
        nonCurrentUser = binding.nonCurrentUser;
        btnMsg = binding.btnMsg;
        btnFollow = binding.btnFollow;
        followingCount = binding.followingCount;
        followerCount = binding.followerCount;
        follower = binding.follower;
        following = binding.following;

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
        FirebaseRecyclerOptions<Post> options =
                new FirebaseRecyclerOptions.Builder<Post>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("userId").equalTo(userId), Post.class)
                        .build();

        adapter = new PostDisplayAdapter(options, fragment);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);
        adapter.notifyDataSetChanged();


        //disp4Cuser
        if (userId.equals(Cuid)) {
            //save, logOut, edit
            disp4Cuser.setVisibility(View.VISIBLE);
            //msg
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) nonCurrentUser.getLayoutParams();
            layoutParams.height = 0;
            nonCurrentUser.setLayoutParams(layoutParams);
        } else {
            //save, logOut, edit
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) disp4Cuser.getLayoutParams();
            layoutParams.height = 0;
            disp4Cuser.setLayoutParams(layoutParams);
            //msg
            nonCurrentUser.setVisibility(View.VISIBLE);
        }

        //display number of follower
        followerCount.setText("0");
        followerDb = database.getReference().child("follow").orderByChild("followed").equalTo(userId);
        ValueEventListener followDbListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                followerCount.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.e("firebase", "Error getting data", databaseError.toException());
            }
        };
        followerDb.addValueEventListener(followDbListener);

        //display number of following
        followerDb = database.getReference().child("follow").orderByChild("follower").equalTo(userId);
        ValueEventListener followingDbListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                followingCount.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.e("firebase", "Error getting data", databaseError.toException());
            }
        };
        followerDb.addValueEventListener(followingDbListener);


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
            intent.putExtra("tag", "profile edit");
            startActivity(intent);

        });


        //logout
        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), StartActivity.class));
        });

        //message button
        btnMsg.setOnClickListener(v -> {
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.Fprofile, new MessageFragment(userId))
                    .addToBackStack(null)
                    .commit();

        });


        //follow button
        //db ref
        DatabaseReference followDbRef = database.getReference().child("follow");
        Query followerQuery = followDbRef.child(Cuid + userId);
        ValueEventListener followerQueryListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Follow post = dataSnapshot.getValue(Follow.class);
                if (post != null) {
                    btnFollow.setText("Following");
                }
                // ..
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        followerQuery.addValueEventListener(followerQueryListener);

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnFollow.getText().equals("follow")) {
                    Map<Object, String> hashMap = new HashMap<>();
                    hashMap.put("follower", Cuid);
                    hashMap.put("followed", userId);
                    followDbRef.child(Cuid + userId).setValue(hashMap);
                } else {
                    followDbRef.child(Cuid + userId).removeValue();
                    btnFollow.setText("follow");
                }
            }
        });

        //onclick disp followers
        follower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.Fprofile, new FollowersFragment(userId, Cuid, "ProfileFragment"), "ProfileFragment")
                            .addToBackStack(null)
                            .commit();

                //....

            }
        });
        //onclick disp following
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                //....
                if (fragment == "HomeFragment"){
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.home, new FollowingFragment(userId, Cuid, "HomeFragment"), "SearchFragment")
                            .addToBackStack(null)
                            .commit();
                }
            if (fragment == "SearchFragment"){
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.Fsearch, new FollowingFragment(userId, Cuid, "SearchFragment"), "SearchFragment")
                        .addToBackStack(null)
                        .commit();
            }else {
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.Fprofile, new FollowingFragment(userId, Cuid, "ProfileFragments"), "ProfileFragments")
                        .addToBackStack(null)
                        .commit();
            }
            }
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