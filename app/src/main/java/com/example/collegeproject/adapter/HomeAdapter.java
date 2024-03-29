package com.example.collegeproject.adapter;

import static android.content.ContentValues.TAG;

import android.media.MediaPlayer;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collegeproject.Model.Follow;
import com.example.collegeproject.Model.Post;
import com.example.collegeproject.Model.UserProfile;
import com.example.collegeproject.R;
import com.example.collegeproject.ui.CommentsDispFragment;
import com.example.collegeproject.ui.HomeFragment;
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

public class HomeAdapter extends FirebaseRecyclerAdapter<Post, HomeAdapter.MyViewHolder> {
    String fragment;
    String name, dp;

    public HomeAdapter(@NonNull FirebaseRecyclerOptions<Post> options, String homeFragment) {
        super(options);
        this.fragment = homeFragment;
    }

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String Cuid = user.getUid();
    //likes ref
    DatabaseReference likesRef = database.getReference("likes");
    DatabaseReference saveRef = database.getReference("saved");
    private DatabaseReference userProfileDB, followDb;

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Post model) {

        followDb = database.getReference().child("follow").child(Cuid + model.getUserId());
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Follow Follow = dataSnapshot.getValue(Follow.class);
                if (Follow != null) {
                    userProfileDB = FirebaseDatabase.getInstance().getReference().child("userProfile").child(model.getUserId());
                    //adding data into Views like TextView, imageView
                    ValueEventListener profileListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get Post object and use the values to update the UI
                            UserProfile UserProfile = dataSnapshot.getValue(UserProfile.class);
                            name = UserProfile.getName();
                            dp = UserProfile.getUri();

                            String Cuid = user.getUid();

                            holder.name.setText(name);
                            holder.desc.setText(model.getDescriptionText());
                            holder.likesCount.setText(model.getLikes() + " Likes");
                            holder.loc.setText(model.getLocation());

                            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                            calendar.setTimeInMillis(Long.parseLong(model.getTimeStamp()));
                            String timedate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
                            holder.time.setText(timedate);

                            //loading image URL to imageView
                            if (model.getImageUrl() == null) {
                                holder.postImg.setVisibility(View.GONE);
                                holder.videoView.setVisibility(View.VISIBLE);
                                holder.videoProgress.setVisibility(View.VISIBLE);
                                String videoPath = model.getVideoUrl();
                                Uri uri = Uri.parse(videoPath);
                                holder.videoView.setVideoURI(uri);
                                holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mp.start();
                                        mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {

                                            @Override
                                            public void onVideoSizeChanged(MediaPlayer mp, int arg1, int arg2) {
                                                // TODO Auto-generated method stub
                                                Log.e(TAG, "Changed");
                                                holder.videoProgress.setVisibility(View.GONE);
                                                mp.start();
                                                MediaController mediaController = new MediaController(holder.itemView.getContext());
                                                holder.videoView.setMediaController(mediaController);
                                                mediaController.setAnchorView(holder.videoView);
                                            }
                                        });
                                    }
                                });

                            }
                            Glide.with(holder.postImg.getContext()).load(model.getImageUrl()).into(holder.postImg);
                            Glide.with(holder.userDP.getContext()).load(dp).into(holder.userDP);

                            //Dp & name onclick
                            holder.profileNav.setOnClickListener(v -> {
                                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                                //....
                                if (fragment == "HomeFragment") {
                                    activity.getSupportFragmentManager()
                                            .beginTransaction()
                                            .add(R.id.home, new ProfileFragment("HomeFragment", model.getUserId()), "fragments")
                                            .addToBackStack(null)
                                            .commit();
                                }
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


                            //open comments fragment
                            holder.comments.setOnClickListener(v -> {
                                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                                //....
                                if (fragment == "HomeFragment"){
                                    activity.getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.home, new CommentsDispFragment(model.getPostId(), "HomeFragment"), "fragments")
                                            .addToBackStack(null)
                                            .commit();
                                }else {
                                    activity.getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.Fprofile, new CommentsDispFragment(model.getPostId(), "fragment"), "fragments")
                                            .addToBackStack(null)
                                            .commit();
                                }

                                //...

                            });
                            //likesCount onclickListener
                            holder.likesCount.setOnClickListener(v -> {
                                int likeCheckZero = Integer.parseInt(model.getLikes());
                                if (likeCheckZero != 0) {
                                    //fragment switching
                                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                                    if (fragment == "HomeFragment") {
                                        activity.getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.home, new LikeDispFragment(model.getPostId()))
                                                .addToBackStack("back")
                                                .commit();
                                    } else {
                                        activity.getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.Fprofile, new LikeDispFragment(model.getPostId()))
                                                .addToBackStack("back")
                                                .commit();
                                    }

                                } else {
                                    Toast.makeText(v.getContext(), "zero Likes", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        }
                    };
                    userProfileDB.addValueEventListener(profileListener);
                } else {
                    holder.postView.setVisibility(View.GONE);
                    holder.postView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        followDb.addValueEventListener(postListener);


        ///..

    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //reference of single_row.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_post, parent, false);
        return new MyViewHolder(view);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        //collect the single_row.xml data with help of ID
        ImageView userDP, postImg, likesImg, comments, save;
        TextView name, desc, likesCount, loc, time;
        LinearLayout profileNav, postView, cvPost;
        VideoView videoView;
        ProgressBar videoProgress;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.postVideoView);
            videoProgress = itemView.findViewById(R.id.videoProgress);
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
            profileNav = itemView.findViewById(R.id.profileNav);
            postView = itemView.findViewById(R.id.LinearLayoutPost);
            cvPost = itemView.findViewById(R.id.cvPost);

        }
    }
}
