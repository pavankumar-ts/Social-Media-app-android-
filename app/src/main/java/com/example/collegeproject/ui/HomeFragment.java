package com.example.collegeproject.ui;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.collegeproject.DenialActivity;

import com.example.collegeproject.Model.Post;
import com.example.collegeproject.StartActivity;
import com.example.collegeproject.adapter.HomeAdapter;
import com.example.collegeproject.databinding.FragmentHomeBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    RecyclerView recyclerView;
    HomeAdapter adapter;

    //current user
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



    @SuppressLint("NotifyDataSetChanged")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //time
        Long timeStamp = System.currentTimeMillis();

        String amPm = DateFormat.format("aa", timeStamp).toString().toLowerCase();
        String hourString = (String) DateFormat.format("hh", timeStamp);
        int hours = Integer.parseInt(hourString);
//        if (amPm.equals("am")){
//            startActivity(new Intent(getActivity(), DenialActivity.class));
//            Log.d(TAG, "hoursStart" + hours);
//        }
//        if (amPm.equals("pm")) {
//            Log.d(TAG, "amPm" + amPm);
//            if (hours < 6 || hours >= 9) {
//                Log.d(TAG, "hours" + hours);
//                startActivity(new Intent(getActivity(), DenialActivity.class));
//                getActivity().finish();
//            }
//        }


        //recyclerView
        recyclerView = binding.recycleView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        FirebaseRecyclerOptions<Post> options =
                new FirebaseRecyclerOptions.Builder<Post>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("posts"), Post.class)
                        .build();
        adapter = new HomeAdapter(options, "HomeFragment");
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return root;

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}