package com.example.collegeproject.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.collegeproject.Model.ModelLikes;
import com.example.collegeproject.Model.ModelPost;
import com.example.collegeproject.R;
import com.example.collegeproject.adapter.LikesAdapter;
import com.example.collegeproject.adapter.PostAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LikeDispFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LikeDispFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;
    LikesAdapter adapter;
    ImageView goBack;
    String postId;

    //Firebase
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    public LikeDispFragment() {
        // Required empty public constructor
    }

    public LikeDispFragment(String postId) {
        this.postId = postId;
    }

    public static LikeDispFragment newInstance(String param1, String param2) {
        LikeDispFragment fragment = new LikeDispFragment();
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

        View view = inflater.inflate(R.layout.fragment_like_disp, container, false);
        //current user
        String Cuid = user.getUid();
        //goback;
        goBack = view.findViewById(R.id.goBack);
        goBack.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });

        recyclerView = view.findViewById(R.id.likeRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        FirebaseRecyclerOptions<ModelLikes> options =
                new FirebaseRecyclerOptions.Builder<ModelLikes>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("likes").orderByChild("postId").equalTo(postId), ModelLikes.class)
                        .build();

        adapter = new LikesAdapter(options);
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