package com.example.collegeproject.adapter;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collegeproject.Model.Comment;
import com.example.collegeproject.Model.UserProfile;
import com.example.collegeproject.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

public class CommentAdapter extends FirebaseRecyclerAdapter<Comment, CommentAdapter.MyViewHolder> {
    //firebase
    private DatabaseReference userDB;

    public CommentAdapter(@NonNull FirebaseRecyclerOptions<Comment> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentAdapter.MyViewHolder holder, int position, @NonNull Comment model) {
        //fetech DP from userProfile collection
        String commentedUser = model.getUserId();
        userDB = FirebaseDatabase.getInstance().getReference().child("userProfile").child(commentedUser);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);

                Log.w("firebase", "data...."+ userProfile.getName());
                Glide.with(holder.userDP.getContext()).load(userProfile.getUri()).into(holder.userDP);
                holder.name.setText(userProfile.name);
                holder.Ctext.setText(model.getCommentsText());


                //converting milliSecond to date and time
                Calendar timeStamp = Calendar.getInstance(Locale.ENGLISH);
                timeStamp.setTimeInMillis(Long.parseLong(String.valueOf(model.getTimeStamp())));
                String timedate = DateFormat.format("dd/MM/yyyy hh:mm", timeStamp).toString();

                holder.time.setText(timedate);
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_comment, parent, false);
        return new MyViewHolder(view);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        //collect the single_row.xml data with help of ID
        ImageView userDP;
        TextView name, Ctext, time;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userDP = itemView.findViewById(R.id.commnetDp);
            name = itemView.findViewById(R.id.name);
            Ctext = itemView.findViewById(R.id.comment);
            time = itemView.findViewById(R.id.CtimeStamp);
        }
    }
}
