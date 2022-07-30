package com.example.collegeproject.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.collegeproject.Model.Follow;
import com.example.collegeproject.R;
import com.example.collegeproject.adapter.FollowAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class FollowersFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    String userId, Cuid, fragment;
    RecyclerView recyclerView;
    FollowAdapter adapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FollowersFragment() {
        // Required empty public constructor
    }

    public FollowersFragment(String userId, String cuid, String fragment) {
        this.Cuid = cuid;
        this.userId = userId;
        this.fragment = fragment;

    }

    public static FollowersFragment newInstance(String param1, String param2) {
        FollowersFragment fragment = new FollowersFragment();
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
        View view = inflater.inflate(R.layout.fragment_followers, container, false);
        //..

        recyclerView = view.findViewById(R.id.rvFollower);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        FirebaseRecyclerOptions<Follow> options =
                new FirebaseRecyclerOptions.Builder<Follow>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("follow").orderByChild("followed").equalTo(userId), Follow.class)
                        .build();

        adapter = new FollowAdapter(options, "followed", fragment);
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