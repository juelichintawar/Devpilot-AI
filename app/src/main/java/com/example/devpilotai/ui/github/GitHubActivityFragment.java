package com.example.devpilotai.ui.github;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.devpilotai.databinding.FragmentGithubActivityBinding;
import com.example.devpilotai.viewmodel.GitHubViewModel;

public class GitHubActivityFragment extends Fragment {

    private FragmentGithubActivityBinding binding;
    private GitHubViewModel viewModel;
    private GitHubActivityAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGithubActivityBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(GitHubViewModel.class);

        adapter = new GitHubActivityAdapter();
        binding.activityRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.activityRecyclerView.setAdapter(adapter);

        viewModel.getUserProfile().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                loadActivity(user.getLogin());
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }

    private void loadActivity(String username) {
        viewModel.getUserActivity(username).observe(getViewLifecycleOwner(), activities -> {
            if (activities != null && !activities.isEmpty()) {
                adapter.setActivities(activities);
                binding.emptyStateText.setVisibility(View.GONE);
            } else {
                binding.emptyStateText.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
