package com.example.collegeproject.adapter;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.collegeproject.Model.ModelPost;
import com.example.collegeproject.R;
import com.example.collegeproject.ui.CommentsDispFragment;
import com.example.collegeproject.ui.LikeDispFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.text.format.DateFormat;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PostAdapter extends FirebaseRecyclerAdapter<ModelPost, PostAdapter.MyViewHolder> {
    String fragment;
    //fragments
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;


    public PostAdapter(@NonNull FirebaseRecyclerOptions<ModelPost> options, String fragment) {
        super(options);
        this.fragment = fragment;

    }


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    //likes ref
    DatabaseReference likesRef = database.getReference("likes");
    DatabaseReference saveRef = database.getReference("saved");



    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, final int position, @NonNull ModelPost model) {
        //adding data into Views like TextView, imageView

        //current user
        String Cuid = user.getUid();

        holder.name.setText(model.getName());
        holder.desc.setText(model.getDescriptionText());
        holder.likesCount.setText(model.getLikes() + " Likes");
        holder.loc.setText(model.getLocation());

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(model.getTimeStamp()));
        String timedate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
        holder.time.setText(timedate);

        //loading image URL to imageView
        Glide.with(holder.postImg.getContext()).load(model.getImageUrl()).into(holder.postImg);
        Glide.with(holder.userDP.getContext()).load(model.getDpUrl()).into(holder.userDP);

        //Dp onclick
        holder.userDP.setOnClickListener(v -> {



        });

        //like imageView
        Query queryLiked = likesRef.child(Cuid + model.getPostId());
        queryLiked.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    Glide.with(holder.likesImg.getContext()).load(R.drawable.ic_likes_icon).into(holder.likesImg);
                } else {
                    Glide.with(holder.likesImg.getContext()).load(R.drawable.ic_like_red_icon).into(holder.likesImg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //save imageView
        Query querySave = saveRef.child(Cuid + model.getPostId());
        querySave.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    Glide.with(holder.save.getContext()).load(R.drawable.ic_baseline_save_24).into(holder.save);
                } else {
                    Glide.with(holder.save.getContext()).load(R.drawable.ic_baseline_save_blue_24).into(holder.save);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // like img onClickListener
        holder.likesImg.setOnClickListener(v -> {
            //writing data to  like collection

            //post ref
            DatabaseReference postsLikes = database.getReference("posts").child(model.getPostId());

            String likeChild = Cuid + model.getPostId();
            Map<Object, String> likeMap = new HashMap<>();
            Query query = likesRef.child(likeChild);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Query queryLikes = postsLikes.child("likes");
                    // updating like and disLike
                    if (dataSnapshot.getValue() == null) {
                        likeMap.put("postId", model.getPostId());
                        likeMap.put("userId", Cuid);
                        likesRef.child(likeChild).setValue(likeMap);
                        //count likes
                        queryLikes.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int likeCount = Integer.parseInt(String.valueOf(snapshot.getValue())) + 1;
                                postsLikes.child("likes").setValue(Integer.toString(likeCount));
                                Log.d(TAG, "likes..,.,,," + likeCount);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d(TAG, "error");
                            }
                        });

                    } else {
                        dataSnapshot.getRef().removeValue();
                        queryLikes.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue() != "0") {
                                    Integer likeCount = Integer.parseInt(String.valueOf(snapshot.getValue())) - 1;
                                    postsLikes.child("likes").setValue(likeCount.toString());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d(TAG, "error");
                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "error");
                }
            });
        });


        //save post
        holder.save.setOnClickListener( v -> {
            // Save Ref

            String saveChild = Cuid + model.getPostId();
            Map<Object, String> saveMap = new HashMap<>();
            Query query = saveRef.child(saveChild);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // updating save
                    if (dataSnapshot.getValue() == null) {
                        saveMap.put("postId", model.getPostId());
                        saveMap.put("userId", Cuid);
                        saveMap.put("name", model.getName());
                        saveMap.put("dpUrl", model.getDpUrl());
                        saveMap.put("descriptionText", model.getDescriptionText());
                        saveMap.put("imageUrl", model.getImageUrl());
                        saveMap.put("likes", model.getLikes());
                        saveMap.put("location", model.getLocation());
                        saveMap.put("timeStamp", model.getTimeStamp());
                        saveRef.child(saveChild).setValue(saveMap);
                        Log.d(TAG, "onDataChange: "+dataSnapshot.getValue());
                        Glide.with(holder.save.getContext()).load(R.drawable.ic_baseline_save_blue_24).into(holder.save);
                    } else {
                        dataSnapshot.getRef().removeValue();
                        Glide.with(holder.save.getContext()).load(R.drawable.ic_baseline_save_24).into(holder.save);
                        Log.d(TAG, "onDataChange: remove,... "+dataSnapshot.getValue());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "error");
                }
            });
        });


        //open comments fragment
        holder.comments.setOnClickListener(v -> {
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            //....
            if (fragment == "ProfileFragment") {
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.Fprofile, new CommentsDispFragment(model.getPostId(), "ProfileFragment"), "fragments")
                        .addToBackStack(null)
                        .commit();
                Toast.makeText(v.getContext(), " not home ", Toast.LENGTH_SHORT).show();
            } else {
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.home, new CommentsDispFragment(model.getPostId(), "HomeFragment"), "fragments")
                        .addToBackStack(null)
                        .commit();
                Toast.makeText(v.getContext(), " home", Toast.LENGTH_SHORT).show();
            }


            //...

        });
        //likesCount onclickListener
        holder.likesCount.setOnClickListener(v -> {
            int likeCheckZero = Integer.parseInt(model.getLikes());
            if (likeCheckZero != 0) {
                //fragment switching
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.home, new LikeDispFragment(model.getPostId()))
                        .addToBackStack("back")
                        .commit();
                Log.i(TAG, "onBindViewHolder: " + model.getLikes());
            } else {
                Toast.makeText(v.getContext(), "zero Likes", Toast.LENGTH_SHORT).show();
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
        //reference of single_row.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_home, parent, false);
        return new MyViewHolder(view);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        //collect the single_row.xml data with help of ID
        ImageView userDP, postImg, likesImg, comments, save;
        TextView name, desc, likesCount, loc, time;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userDP = itemView.findViewById(R.id.userDP);
            postImg = itemView.findViewById(R.id.postImg);
            likesImg = itemView.findViewById(R.id.likesImg);
            comments = itemView.findViewById(R.id.comments);
            name = itemView.findViewById(R.id.userName);
            desc = itemView.findViewById(R.id.description);
            likesCount = itemView.findViewById(R.id.likesCount);
            loc = itemView.findViewById(R.id.locHome);
            time = itemView.findViewById(R.id.time);
            save = itemView.findViewById(R.id.save);

        }
    }

}
