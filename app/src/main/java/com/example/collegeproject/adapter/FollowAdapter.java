package com.example.collegeproject.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collegeproject.Model.Follow;
import com.example.collegeproject.Model.UserProfile;
import com.example.collegeproject.R;
import com.example.collegeproject.ui.ProfileFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FollowAdapter  extends FirebaseRecyclerAdapter<Follow, FollowAdapter.MyViewHolder> {
    private DatabaseReference userDB;
    String followOption, userId;

    public FollowAdapter(@NonNull FirebaseRecyclerOptions<Follow> options, String followOption) {
        super(options);
        this.followOption = followOption;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Follow model) {

        //fetech DP from userProfile collection

        if (followOption.equals("followed")){
            userId = model.getFollower();
        }else {
            userId = model.getFollowed();
        }
        userDB = FirebaseDatabase.getInstance().getReference().child("userProfile").child(userId);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                Glide.with(holder.dp.getContext()).load(userProfile.getUri()).into(holder.dp);
                holder.name.setText(userProfile.name);
                // ..
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.e("firebase", "Error getting data", databaseError.toException());
            }
        };
        userDB.addValueEventListener(postListener);

        //open userProfile
        holder.cvFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getUserId;
                if (followOption.equals("followed")){
                    getUserId = model.getFollower();
                }else {
                    getUserId = model.getFollowed();
                }

                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                //....
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.nav_host_fragment_activity_dashboard, new ProfileFragment("HomeFragment", getUserId), "FollowFragments")
                        .addToBackStack(null)
                        .commit();
            }
        });
    }


    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_follow, parent, false);
        return new MyViewHolder(view);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView dp;
        TextView name;
        CardView cvFollow;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dp = itemView.findViewById(R.id.followDp);
            name = itemView.findViewById(R.id.followName);
            cvFollow = itemView.findViewById(R.id.cvFollow);
        }
    }
}
