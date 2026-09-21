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

import com.example.devpilotai.R;
import com.example.devpilotai.data.model.User;
import com.example.devpilotai.databinding.FragmentAnalyticsBinding;
import com.example.devpilotai.viewmodel.AnalyticsViewModel;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsFragment extends BaseAdminFragment {

    private FragmentAnalyticsBinding binding;
    private AnalyticsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAnalyticsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected void onAdminVerified(User adminUser) {
        viewModel = new ViewModelProvider(this).get(AnalyticsViewModel.class);

        setupToolbar();
        observeViewModel();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void observeViewModel() {
        viewModel.getAnalyticsData().observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                updateUI(data);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.loadingOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(AnalyticsViewModel.AnalyticsData data) {
        binding.tvDau.setText(String.valueOf(data.dailyActiveUsers));
        binding.tvTotalUsers.setText(String.valueOf(data.totalUsers));
        binding.tvMostUsed.setText(data.mostUsedModule);
        binding.tvFailed.setText(String.valueOf(data.failedRequests));

        binding.tvAiCount.setText(String.valueOf(data.aiRequests));
        binding.tvOcrCount.setText(String.valueOf(data.ocrScans));
        binding.tvGithubCount.setText(String.valueOf(data.githubActions));

        int max = Math.max(data.aiRequests, Math.max(data.ocrScans, data.githubActions));
        if (max > 0) {
            binding.pbAi.setMax(max);
            binding.pbAi.setProgress(data.aiRequests);
            binding.pbOcr.setMax(max);
            binding.pbOcr.setProgress(data.ocrScans);
            binding.pbGithub.setMax(max);
            binding.pbGithub.setProgress(data.githubActions);
        }

        // Update Chart
        List<SimpleBarChartView.BarData> chartData = new ArrayList<>();
        if (data.activityChartData != null) {
            String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            for (String day : days) {
                if (data.activityChartData.containsKey(day)) {
                    chartData.add(new SimpleBarChartView.BarData(day, data.activityChartData.get(day), getContext().getColor(R.color.primary)));
                }
            }
            binding.activityChart.setData(chartData);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
