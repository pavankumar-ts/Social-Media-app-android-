package com.example.collegeproject.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collegeproject.Model.Msg;
import com.example.collegeproject.Model.UserProfile;
import com.example.collegeproject.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MsgListAdapter extends FirebaseRecyclerAdapter<UserProfile, MsgListAdapter.MyViewHolder> {

    private Query messageDB;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String Cuid = user.getUid();


    public MsgListAdapter(@NonNull FirebaseRecyclerOptions<UserProfile> options) {
        super(options);
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_dp_name, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull UserProfile model) {
        holder.linearLayout.setVisibility(View.GONE);
        messageDB = FirebaseDatabase.getInstance().getReference().child("message").orderByChild("receiver").equalTo(Cuid);

        messageDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Msg data = dataSnapshot.getValue(Msg.class);
                Log.d("Tag", "data.."+data.getSender());
                Log.d("Tag", "data.."+model.getUserId());
                if (model.getUserId().equals(data.getSender())){
                    holder.linearLayout.setVisibility(View.VISIBLE);
                    Glide.with(holder.dp.getContext()).load(model.getUri()).into(holder.dp);
                    holder.name.setText(model.getName());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ERROR", String.valueOf(error));
            }

            // ...
        });

    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView dp;
        TextView name;
        LinearLayout linearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dp = itemView.findViewById(R.id.Dp);
            name = itemView.findViewById(R.id.Name);
            linearLayout = itemView.findViewById(R.id.linearLayoutDp);
        }
    }
}
