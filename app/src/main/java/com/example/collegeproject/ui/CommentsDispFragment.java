package com.example.collegeproject.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.collegeproject.Model.ModelComment;
import com.example.collegeproject.R;
import com.example.collegeproject.adapter.CommentAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class CommentsDispFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    //fragment
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;
    CommentAdapter adapter;
    String postId, fragment;
    ImageView goBack, postComments;
    TextView comment;
    //DB
    private DatabaseReference CommentDb;
    // ...auth
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    public CommentsDispFragment() {
        // Required empty public constructor
    }

    public CommentsDispFragment(String postId, String fragment) {
        this.fragment = fragment;
        this.postId = postId;
    }

    public static CommentsDispFragment newInstance(String param1, String param2) {
        CommentsDispFragment fragment = new CommentsDispFragment();
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
        View view = inflater.inflate(R.layout.fragment_comments_disp, container, false);

        //recyclerView
        recyclerView = view.findViewById(R.id.commentsRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        FirebaseRecyclerOptions<ModelComment> options =
                new FirebaseRecyclerOptions.Builder<ModelComment>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("comments").orderByChild("postId").equalTo(postId)
                                , ModelComment.class)
                        .build();
        adapter = new CommentAdapter(options);
        recyclerView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();


        //goback;
        goBack = view.findViewById(R.id.goback);
        goBack.setOnClickListener(v -> {
                getActivity().getSupportFragmentManager().popBackStack();
        });
        //TextView Comment
        comment = view.findViewById(R.id.tvComments);

        //postComments
        postComments = view.findViewById(R.id.postComments);
        postComments.setOnClickListener(v -> {
            //current user
            String Cuid = user.getUid();
            String txtComment = comment.getText().toString();
            final String timestamp = String.valueOf(System.currentTimeMillis());

            CommentDb = FirebaseDatabase.getInstance().getReference("comments");
            Map<Object, String> commentData = new HashMap<>();
            commentData.put("commentsText", txtComment);
            commentData.put("postId", postId);
            commentData.put("userId", Cuid);
            commentData.put("timeStamp", timestamp);
            CommentDb.push().setValue(commentData);
            comment.setText("");

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