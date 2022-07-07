package com.example.collegeproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collegeproject.Model.ModelComment;
import com.example.collegeproject.Model.ModelPost;
import com.example.collegeproject.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class CommentAdapter extends FirebaseRecyclerAdapter<ModelComment, CommentAdapter.MyViewHolder> {

    public CommentAdapter(@NonNull FirebaseRecyclerOptions<ModelComment> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentAdapter.MyViewHolder holder, int position, @NonNull ModelComment model) {

    }


    @NonNull
    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_likes, parent, false);
        return new CommentAdapter.MyViewHolder(view);
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        //collect the single_row.xml data with help of ID
        ImageView userDP, postImg, likesImg, comments;
        TextView name, desc, likesCount, loc, time;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

        }
    }
}
