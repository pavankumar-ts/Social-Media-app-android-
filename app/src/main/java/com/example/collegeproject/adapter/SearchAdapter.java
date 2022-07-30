package com.example.collegeproject.adapter;


import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collegeproject.Model.Likes;
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

public class SearchAdapter extends FirebaseRecyclerAdapter<UserProfile, SearchAdapter.MyViewHolder> {


    public SearchAdapter(@NonNull FirebaseRecyclerOptions<UserProfile> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull UserProfile model) {
        //fetech DP from userProfile collection
        Log.d("TAG"," get name"+model.getName());
        Glide.with(holder.dp.getContext()).load(model.getUri()).into(holder.dp);
        holder.name.setText(model.name);


        //Dp name onclickListener
        holder.linearLayoutDp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                //....
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.Fsearch, new ProfileFragment("SearchFragment", model.userId), "FollowFragments")
                        .addToBackStack(null)
                        .commit();


                //hide keyBoard
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                View view = activity.getCurrentFocus();
                if (view == null) {
                    view = new View(activity);
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_dp_name, parent, false);
        return new MyViewHolder(view);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView dp;
        TextView name;
        LinearLayout linearLayoutDp;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dp = itemView.findViewById(R.id.Dp);
            name = itemView.findViewById(R.id.Name);
            linearLayoutDp = itemView.findViewById(R.id.linearLayoutDp);
        }
    }
}
