package com.example.collegeproject.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collegeproject.Model.Follow;
import com.example.collegeproject.Model.Likes;
import com.example.collegeproject.Model.UserProfile;
import com.example.collegeproject.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LikesAdapter extends FirebaseRecyclerAdapter<Likes, LikesAdapter.MyViewHolder> {
    private DatabaseReference userDB;

    public LikesAdapter(@NonNull FirebaseRecyclerOptions<Likes> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Likes model) {
        //fetech DP from userProfile collection
        userDB = FirebaseDatabase.getInstance().getReference().child("userProfile").child(model.getUserId());
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
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

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_likes, parent, false);
        return new MyViewHolder(view);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView dp;
        TextView name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dp = itemView.findViewById(R.id.likeDp);
            name = itemView.findViewById(R.id.likeName);
        }
    }
}
