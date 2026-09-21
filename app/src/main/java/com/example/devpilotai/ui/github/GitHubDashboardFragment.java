package com.example.devpilotai.ui.github;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.devpilotai.R;
import com.example.devpilotai.databinding.FragmentGithubDashboardBinding;
import com.example.devpilotai.viewmodel.GitHubViewModel;
import com.google.android.material.tabs.TabLayoutMediator;
import androidx.lifecycle.ViewModelProvider;

public class GitHubDashboardFragment extends Fragment {

    private FragmentGithubDashboardBinding binding;
    private GitHubViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGithubDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(GitHubViewModel.class);

        setupViewPager();
        setupToolbar();
    }

    private void setupViewPager() {
        binding.viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0: return new GitHubRepoListFragment();
                    case 1: return new GitHubProfileFragment();
                    case 2: return new GitHubActivityFragment();
                    default: return new GitHubRepoListFragment();
                }
            }

            @Override
            public int getItemCount() {
                return 3;
            }
        });

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Repositories"); break;
                case 1: tab.setText("Profile"); break;
                case 2: tab.setText("Activity"); break;
            }
        }).attach();
    }

    private void setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_github_logout) {
                viewModel.logout();
                Navigation.findNavController(requireView()).navigate(R.id.action_githubDashboardFragment_to_githubLoginFragment);
                return true;
            }
            return false;
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
