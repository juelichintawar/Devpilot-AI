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

import com.example.devpilotai.data.model.User;
import com.example.devpilotai.databinding.FragmentOcrMonitoringBinding;
import com.example.devpilotai.viewmodel.OcrMonitoringViewModel;

public class OcrMonitoringFragment extends BaseAdminFragment {

    private FragmentOcrMonitoringBinding binding;
    private OcrMonitoringViewModel viewModel;
    private OcrLogAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOcrMonitoringBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected void onAdminVerified(User adminUser) {
        viewModel = new ViewModelProvider(this).get(OcrMonitoringViewModel.class);

        setupToolbar();
        setupRecyclerView();
        observeViewModel();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void setupRecyclerView() {
        adapter = new OcrLogAdapter();
        binding.rvOcrLogs.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvOcrLogs.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getLogs().observe(getViewLifecycleOwner(), logs -> {
            adapter.setLogs(logs);
            binding.tvEmpty.setVisibility(logs == null || logs.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getStats().observe(getViewLifecycleOwner(), stats -> {
            if (stats != null) {
                binding.tvTotalScans.setText(String.valueOf(stats.totalScans));
                binding.tvSuccessScans.setText(String.valueOf(stats.successfulScans));
                binding.tvFailedScans.setText(String.valueOf(stats.failedScans));
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
