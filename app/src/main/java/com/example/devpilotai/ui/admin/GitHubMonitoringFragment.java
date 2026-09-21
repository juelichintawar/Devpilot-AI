package com.example.devpilotai.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.devpilotai.R;
import com.example.devpilotai.data.model.User;
import com.example.devpilotai.databinding.FragmentGithubMonitoringBinding;
import com.example.devpilotai.viewmodel.GitHubMonitoringViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GitHubMonitoringFragment extends BaseAdminFragment {

    private FragmentGithubMonitoringBinding binding;
    private GitHubMonitoringViewModel viewModel;
    private GitHubLogAdapter adapter;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGithubMonitoringBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected void onAdminVerified(User adminUser) {
        viewModel = new ViewModelProvider(this).get(GitHubMonitoringViewModel.class);

        setupToolbar();
        setupRecyclerView();
        observeViewModel();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void setupRecyclerView() {
        adapter = new GitHubLogAdapter();
        binding.rvGitHubLogs.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvGitHubLogs.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getLogs().observe(getViewLifecycleOwner(), logs -> {
            adapter.setLogs(logs);
            binding.tvEmpty.setVisibility(logs == null || logs.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getStats().observe(getViewLifecycleOwner(), stats -> {
            if (stats != null) {
                binding.tvConnectedUsers.setText(String.valueOf(stats.connectedUsers));
                binding.tvTotalActions.setText(String.valueOf(stats.totalActions));
                if (stats.lastSyncTimestamp > 0) {
                    binding.tvLastSync.setText("Last Activity: " + dateFormat.format(new Date(stats.lastSyncTimestamp)));
                } else {
                    binding.tvLastSync.setText("Last Activity: N/A");
                }
            }
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
