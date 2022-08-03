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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collegeproject.Model.Msg;
import com.example.collegeproject.Model.UserProfile;
import com.example.collegeproject.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

public class MsgAdapter extends FirebaseRecyclerAdapter<Msg, MsgAdapter.MyViewHolder> {
    private static final int MSG_TYPR_LEFT = 0;
    private static final int MSG_TYPR_RIGHT = 1;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String Cuid = user.getUid();
    String Ruid;
    //database
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public MsgAdapter(@NonNull FirebaseRecyclerOptions<Msg> options, String ruserId) {
        super(options);
        this.Ruid = ruserId;
    }

    @Override
    protected void onBindViewHolder(@NonNull MsgAdapter.MyViewHolder holder, int position, @NonNull Msg model) {
        if (Ruid.equals(model.getReceiver()) && Cuid.equals(model.getSender()) || Cuid.equals(model.getReceiver()) && Ruid.equals(model.getSender())) {
            holder.msg.setText(model.getText());
            //user DB ref
            DatabaseReference userDb = database.getReference().child("userProfile").child(model.getSender());
            //Name
            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserProfile userData = dataSnapshot.getValue(UserProfile.class);
                    Glide.with(holder.dp.getContext()).load(userData.getUri()).into(holder.dp);

                    // ..
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            };
            userDb.addValueEventListener(postListener);
            //time
            Calendar timeStamp = Calendar.getInstance(Locale.ENGLISH);
            timeStamp.setTimeInMillis(Long.parseLong(String.valueOf(model.getTimeStamp())));
            String timedate = DateFormat.format("dd/MM/yyyy hh:mm", timeStamp).toString();
            holder.timeDisp.setText(timedate);


        } else {
            holder.msgSingleRow.setVisibility(View.GONE);
            holder.msgSingleRow.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }



    }

    @NonNull
    @Override
    public MsgAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MSG_TYPR_RIGHT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_msg_right, parent, false);
            return new MyViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_msg_left, parent, false);
            return new MyViewHolder(view);
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        Msg model = getItem(position);
        if (model.getSender().equals(Cuid)) {
            return MSG_TYPR_RIGHT;
        }
        return MSG_TYPR_LEFT;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView dp;
        TextView msg, timeDisp;
        LinearLayout msgSingleRow;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dp = itemView.findViewById(R.id.dp);
            msg = itemView.findViewById(R.id.msg);
            msgSingleRow = itemView.findViewById(R.id.msgSingleRow);
            timeDisp = itemView.findViewById(R.id.timeDisp);
        }
    }
}
