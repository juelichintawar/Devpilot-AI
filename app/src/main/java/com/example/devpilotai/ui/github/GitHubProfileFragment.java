package com.example.devpilotai.ui.github;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.devpilotai.databinding.FragmentGithubProfileBinding;
import com.example.devpilotai.viewmodel.GitHubViewModel;

public class GitHubProfileFragment extends Fragment {

    private FragmentGithubProfileBinding binding;
    private GitHubViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGithubProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(GitHubViewModel.class);

        viewModel.getUserProfile().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.profileName.setText(user.getName() != null ? user.getName() : user.getLogin());
                binding.profileUsername.setText("@" + user.getLogin());
                binding.profileBio.setText(user.getBio());
                binding.followersCount.setText(String.valueOf(user.getFollowers()));
                binding.followingCount.setText(String.valueOf(user.getFollowing()));
                binding.reposCount.setText(String.valueOf(user.getPublicRepos()));

                Glide.with(this)
                        .load(user.getAvatarUrl())
                        .circleCrop()
                        .into(binding.profileImage);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Loading handled by parent if needed, or add local indicator
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
