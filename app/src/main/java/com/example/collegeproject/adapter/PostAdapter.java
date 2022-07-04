package com.example.collegeproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collegeproject.Model.ModelPost;
import com.example.collegeproject.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class PostAdapter extends FirebaseRecyclerAdapter<ModelPost, PostAdapter.MyViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PostAdapter(@NonNull FirebaseRecyclerOptions<ModelPost> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull ModelPost model) {
        //adding data into Views like TextView, imageView
        holder.name.setText(model.getName());
        holder.desc.setText(model.getDescriptionText());
        holder.likesCount.setText(model.getLikes());

        //loading image URL to imageView
        Glide.with(holder.postImg.getContext()).load(model.getImageUrl()).into(holder.postImg);
        Glide.with(holder.userDP.getContext()).load(model.getDpUrl()).into(holder.userDP);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    //reference of single_row.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_home,parent,false);
        return new MyViewHolder(view);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        //collect the single_row.xml data with help of ID
        ImageView userDP, postImg, likesImg, comments;
        TextView name, desc,likesCount;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userDP = itemView.findViewById(R.id.userDP);
            postImg = itemView.findViewById(R.id.postImg);
            likesImg = itemView.findViewById(R.id.likesImg);
            comments = itemView.findViewById(R.id.comments);
            name = itemView.findViewById(R.id.userName);
            desc = itemView.findViewById(R.id.description);
            likesCount = itemView.findViewById(R.id.likesCount);

        }
    }
}
