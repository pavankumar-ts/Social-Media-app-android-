package com.example.collegeproject.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collegeproject.Model.ModelMsg;
import com.example.collegeproject.R;
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
import com.google.firebase.database.core.Tag;

public class MsgAdapter extends FirebaseRecyclerAdapter<ModelMsg, MsgAdapter.MyViewHolder> {
    private static final int MSG_TYPR_LEFT = 0;
    private static final int MSG_TYPR_RIGHT = 1;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String Cuid = user.getUid();
    String Ruid;

    Boolean sender = false;
    //DB
    private DatabaseReference userDB;
    private Query messageDB;

    public MsgAdapter(@NonNull FirebaseRecyclerOptions<ModelMsg> options, String ruserId) {
        super(options);
        this.Ruid = ruserId;
    }

    @Override
    protected void onBindViewHolder(@NonNull MsgAdapter.MyViewHolder holder, int position, @NonNull ModelMsg model) {
        if (Ruid.equals(model.getReceiver()) && Cuid.equals(model.getSender()) || Cuid.equals(model.getReceiver()) && Ruid.equals(model.getSender()) ) {
            holder.msg.setText(model.getText());
        }else {
            holder.msgSingleRow.setVisibility(View.INVISIBLE);
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
        ModelMsg model = getItem(position);
        if (model.getSender().equals(Cuid)) {
            return MSG_TYPR_RIGHT;
        }
        return MSG_TYPR_LEFT;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView dp;
        TextView msg;
        LinearLayout msgSingleRow;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dp = itemView.findViewById(R.id.dp);
            msg = itemView.findViewById(R.id.msg);
            msgSingleRow = itemView.findViewById(R.id.msgSingleRow);
        }
    }
}
