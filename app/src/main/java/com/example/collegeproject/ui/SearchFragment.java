package com.example.collegeproject.ui;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.collegeproject.R;

public class SearchFragment extends Fragment {



    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.search_fragment, container, false);

        TextView tv = view.findViewById(R.id.sTv);
        tv.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController( getActivity(), R.id.nav_host_fragment_activity_dashboard);
            navController.navigate(R.id.action_navigation_search_to_commentsDispFragment);
        });
        return view;
    }


}