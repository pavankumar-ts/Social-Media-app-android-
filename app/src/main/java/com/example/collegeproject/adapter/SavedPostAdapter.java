package com.example.collegeproject.adapter;

import static android.content.ContentValues.TAG;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collegeproject.Model.Post;
import com.example.collegeproject.Model.SavedPost;
import com.example.collegeproject.Model.UserProfile;
import com.example.collegeproject.R;
import com.example.collegeproject.ui.CommentsDispFragment;
import com.example.collegeproject.ui.LikeDispFragment;
import com.example.collegeproject.ui.ProfileFragment;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SavedPostAdapter extends FirebaseRecyclerAdapter<SavedPost, SavedPostAdapter.MyViewHolder> {
    //..
    private DatabaseReference userDB;
    private DatabaseReference postDB;

    //user
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String Cuid = user.getUid();

    //db
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    //likes ref
    DatabaseReference likesRef = database.getReference("likes");
    DatabaseReference saveRef = database.getReference("saved");
    public SavedPostAdapter(@NonNull FirebaseRecyclerOptions<SavedPost> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull SavedPost model) {
        //fetch DP from userProfile collection
        //fetch userProfile
        userDB = FirebaseDatabase.getInstance().getReference().child("userProfile").child(model.getUserId());
        ValueEventListener userProfile = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                holder.name.setText(userProfile.getName());
                Glide.with(holder.dp.getContext()).load(userProfile.getUri()).into(holder.dp);
                // ..
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        userDB.addValueEventListener(userProfile);

        //fetch post data
        postDB = FirebaseDatabase.getInstance().getReference().child("posts").child(model.getPostId());
        ValueEventListener postData = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Post post = dataSnapshot.getValue(Post.class);

                Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                calendar.setTimeInMillis(Long.parseLong(post.getTimeStamp()));
                String timedate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
                holder.time.setText(timedate);

                holder.description.setText(post.getDescriptionText());
                holder.loc.setText(post.getLocation());
                holder.likesCount.setText(post.getLikes() + " Likes");



                //img
                Glide.with(holder.postImg.getContext()).load(post.getImageUrl()).into(holder.postImg);
                // ..
                //Dp & name onclick
                holder.profileNav.setOnClickListener(v -> {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    //....
                        activity.getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.Fprofile, new ProfileFragment("ProfileFragment", post.getUserId()), "fragments")
                                .addToBackStack(null)
                                .commit();
                        Toast.makeText(v.getContext(), " not home ", Toast.LENGTH_SHORT).show();
                });

                //like imageView
                Query queryLiked = likesRef.child(Cuid + post.getPostId());
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

                //open comments fragment
                holder.comments.setOnClickListener(v -> {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    //....
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.Fprofile, new CommentsDispFragment(model.getPostId(), "ProfileFragment"), "fragments")
                            .addToBackStack(null)
                            .commit();
                    //...

                });
                //likesCount onclickListener
                holder.likesCount.setOnClickListener(v -> {
                    int likeCheckZero = Integer.parseInt(post.getLikes());
                    if (likeCheckZero != 0) {
                        //fragment switching
                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        activity.getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.Fprofile, new LikeDispFragment(model.getPostId()))
                                .addToBackStack("back")
                                .commit();
                        Log.i(TAG, "onBindViewHolder: " + post.getLikes());
                    } else {
                        Toast.makeText(v.getContext(), "zero Likes", Toast.LENGTH_SHORT).show();
                    }
                });

                //save post onclick
                holder.save.setOnClickListener(v -> {
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
                                saveMap.put("userId", model.getUserId());
                                saveMap.put("savedUserId", Cuid);
                                saveRef.child(saveChild).setValue(saveMap);
                                Log.d(TAG, "onDataChange: " + dataSnapshot.getValue());
                                Glide.with(holder.save.getContext()).load(R.drawable.ic_baseline_save_blue_24).into(holder.save);
                            } else {
                                dataSnapshot.getRef().removeValue();
                                Glide.with(holder.save.getContext()).load(R.drawable.ic_baseline_save_24).into(holder.save);
                                Log.d(TAG, "onDataChange: remove,... " + dataSnapshot.getValue());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d(TAG, "error");
                        }
                    });
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        postDB.addValueEventListener(postData);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_post, parent, false);
        return new MyViewHolder(view);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView dp, postImg, likesImg, comments, save;
        TextView name, loc, time,  likesCount, description;
        LinearLayout profileNav;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dp = itemView.findViewById(R.id.userDP);
            name = itemView.findViewById(R.id.userName);
            postImg = itemView.findViewById(R.id.postImg);
            loc = itemView.findViewById(R.id.locHome);
            time = itemView.findViewById(R.id.time);
            likesImg = itemView.findViewById(R.id.likesImg);
            comments = itemView.findViewById(R.id.comments);
            save = itemView.findViewById(R.id.save);
            likesCount = itemView.findViewById(R.id.likesCount);
            description = itemView.findViewById(R.id.description);
            profileNav = itemView.findViewById(R.id.profileNav);

        }
    }

}
