package com.example.collegeproject.ui.post;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.collegeproject.databinding.FragmentPostBinding;
import com.example.collegeproject.databinding.FragmentPostBinding;

public class PostFragment extends Fragment {

    private PostViewModel postViewModel;
private FragmentPostBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        postViewModel =
                new ViewModelProvider(this).get(PostViewModel.class);

    binding = FragmentPostBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        postViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}