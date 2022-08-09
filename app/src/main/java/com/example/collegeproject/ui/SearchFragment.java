package com.example.collegeproject.ui;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.collegeproject.DenialActivity;
import com.example.collegeproject.Model.Likes;
import com.example.collegeproject.Model.UserProfile;
import com.example.collegeproject.R;
import com.example.collegeproject.adapter.SearchAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;

public class SearchFragment extends Fragment {

    RecyclerView recyclerView;
    SearchAdapter adapter;
    String getText = "0";
    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_search, container, false);
        EditText text = view.findViewById(R.id.etSearch);
        ImageView icon = view.findViewById(R.id.iconSearch);


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


        recyclerView = view.findViewById(R.id.rvSearch);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        FirebaseRecyclerOptions<UserProfile> options =
                new FirebaseRecyclerOptions.Builder<UserProfile>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("userProfile")
                                .orderByChild("name").startAt(getText).endAt(getText+"\uf8ff"), UserProfile.class)
                        .build();
        adapter = new SearchAdapter(options);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();


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
        adapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}