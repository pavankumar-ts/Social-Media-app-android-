package com.example.collegeproject.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.collegeproject.Model.Likes;
import com.example.collegeproject.Model.UserProfile;
import com.example.collegeproject.R;
import com.example.collegeproject.adapter.SearchAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class SearchFragment extends Fragment {

    RecyclerView recyclerView;
    SearchAdapter adapter;
    String getText;
    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_search, container, false);
        EditText text = view.findViewById(R.id.etSearch);
        ImageView icon = view.findViewById(R.id.iconSearch);
        recyclerView = view.findViewById(R.id.rvSearch);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //..
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getText = text.getText().toString();
                FirebaseRecyclerOptions<UserProfile> options =
                        new FirebaseRecyclerOptions.Builder<UserProfile>()
                                .setQuery(FirebaseDatabase.getInstance().getReference().child("userProfile")
                                        .orderByChild("name").startAt(getText).endAt(getText+"\uf8ff"), UserProfile.class)
                                .build();
                adapter = new SearchAdapter(options);
                recyclerView.setAdapter(adapter);
                adapter.startListening();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //...
            }
        });

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getText = text.getText().toString();
                FirebaseRecyclerOptions<UserProfile> options =
                        new FirebaseRecyclerOptions.Builder<UserProfile>()
                                .setQuery(FirebaseDatabase.getInstance().getReference().child("userProfile")
                                        .orderByChild("name").startAt(getText).endAt(getText+"\uf8ff"), UserProfile.class)
                                .build();
                adapter = new SearchAdapter(options);
                recyclerView.setAdapter(adapter);
                adapter.startListening();
                adapter.notifyDataSetChanged();
            }
        });

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
//        adapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}