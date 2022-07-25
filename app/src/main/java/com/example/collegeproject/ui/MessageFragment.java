package com.example.collegeproject.ui;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.collegeproject.Model.Msg;
import com.example.collegeproject.Model.UserProfile;
import com.example.collegeproject.R;
import com.example.collegeproject.adapter.MsgAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class MessageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //message ref
    DatabaseReference messageRef = database.getReference("message");

    //user
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String Cuid = user.getUid();

    String RuserId;

    TextView msgName;
    EditText msg;
    ImageView postMsg, msgDp;
    RecyclerView recyclerView;
    MsgAdapter adapter;

    public MessageFragment() {
        // Required empty public constructor
    }

    public MessageFragment(String RuserId) {
        this.RuserId = RuserId;
    }

    public static MessageFragment newInstance(String param1, String param2) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);


        postMsg = view.findViewById(R.id.postMsg);
        msg = view.findViewById(R.id.msgText);
        msgName = view.findViewById(R.id.msgName);
        msgDp = view.findViewById(R.id.msgDp);



        //recyclerView
        recyclerView = view.findViewById(R.id.messageRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        FirebaseRecyclerOptions<Msg> options =
                new FirebaseRecyclerOptions.Builder<Msg>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("message")
                                , Msg.class)
                        .build();
        adapter = new MsgAdapter(options, RuserId);
        recyclerView.setAdapter(adapter);

        //user DB ref
        DatabaseReference userDb = database.getReference().child("userProfile").child(RuserId);
        //Name
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userData = dataSnapshot.getValue(UserProfile.class);
                msgName.setText(userData.getName());
                Glide.with(msgDp.getContext()).load(userData.getUri()).into(msgDp);
                // ..
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        userDb.addValueEventListener(postListener);


        postMsg.setOnClickListener(v -> {
            String txtMsg = msg.getText().toString();

            if (!TextUtils.isEmpty(txtMsg)) {
                final String timestamp = String.valueOf(System.currentTimeMillis());
                Map<Object, String> msgData = new HashMap<>();
                msgData.put("text", txtMsg);
                msgData.put("sender", Cuid);
                msgData.put("receiver", RuserId);
                msgData.put("timeStamp", timestamp);
//                msgData.put("token", "token");
                messageRef.push().setValue(msgData);
                msg.setText("");
                adapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());

            } else {
                Toast.makeText(getContext(), "plaese type the message..!", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}