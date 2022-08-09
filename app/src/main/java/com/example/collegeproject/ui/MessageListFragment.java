package com.example.collegeproject.ui;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.collegeproject.DenialActivity;
import com.example.collegeproject.Model.Msg;
import com.example.collegeproject.Model.UserProfile;
import com.example.collegeproject.R;
import com.example.collegeproject.adapter.MsgListAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;

public class MessageListFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    RecyclerView recyclerView;
    MsgListAdapter adapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MessageListFragment() {
        // Required empty public constructor
    }

    public static MessageListFragment newInstance(String param1, String param2) {
        MessageListFragment fragment = new MessageListFragment();
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
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);

        //time
        Long timeStamp = System.currentTimeMillis();
        Calendar time = Calendar.getInstance(Locale.ENGLISH);
        time.setTimeInMillis(Long.parseLong(String.valueOf(timeStamp)));

        String amPm = DateFormat.format("aa", timeStamp).toString().toLowerCase();
        String hourString = (String) DateFormat.format("hh", timeStamp);
        int hours = Integer.parseInt(hourString);

        Log.d(TAG, "hours" + hours);
        if (amPm.equals("pm")) {
            Log.d(TAG, "amPm" + amPm);
            if (hours < 6 || hours >= 9) {
                Log.d(TAG, "hours" + hours);
                startActivity(new Intent(getActivity(), DenialActivity.class));
                getActivity().finish();
            }
        }


        recyclerView = view.findViewById(R.id.rvMessage);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        FirebaseRecyclerOptions<UserProfile> options =
                new FirebaseRecyclerOptions.Builder<UserProfile>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("userProfile"), UserProfile.class)
                        .build();

        adapter = new MsgListAdapter(options);
        recyclerView.setAdapter(adapter);
//        recyclerView.setItemAnimator(null);
        adapter.notifyDataSetChanged();




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