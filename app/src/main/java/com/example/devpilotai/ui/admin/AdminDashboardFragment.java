package com.example.devpilotai.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.devpilotai.R;
import com.example.devpilotai.data.AdminLogRepository;
import com.example.devpilotai.data.model.User;
import com.example.devpilotai.databinding.FragmentAdminDashboardBinding;
import com.example.devpilotai.viewmodel.AdminViewModel;

import java.util.List;

public class AdminDashboardFragment extends BaseAdminFragment {

    private FragmentAdminDashboardBinding binding;
    private AdminViewModel adminViewModel;
    private AdminLogRepository adminLogRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected void onAdminVerified(User adminUser) {
        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        adminLogRepository = new AdminLogRepository();

        observeViewModel();
        setupQuickActions();
        setupCardListeners();
        setupBottomNav();
    }

    private void observeViewModel() {
        adminViewModel.getStats().observe(getViewLifecycleOwner(), stats -> {
            if (stats != null) {
                binding.tvTotalUsersCount.setText(stats.totalUsers);
                binding.tvActiveUsersCount.setText(stats.activeUsers);
                binding.tvTotalChatsCount.setText(stats.totalChats);
                binding.tvOcrUsageCount.setText(stats.ocrUsage);
                binding.tvGithubUsageCount.setText(stats.githubUsage);
                binding.tvSnippetsCount.setText(stats.snippetsCount);
            }
        });

        adminViewModel.getRecentActivities().observe(getViewLifecycleOwner(), this::updateRecentActivities);
    }

    private void updateRecentActivities(List<AdminViewModel.AdminActivity> activities) {
        binding.recentActivityContainer.removeAllViews();
        if (activities != null) {
            for (AdminViewModel.AdminActivity activity : activities) {
                addActivityItem(activity);
            }
        }
    }

    private void addActivityItem(AdminViewModel.AdminActivity activity) {
        View itemView = getLayoutInflater().inflate(R.layout.item_admin_activity, binding.recentActivityContainer, false);
        
        ImageView icon = itemView.findViewById(R.id.ivActivityIcon);
        TextView tvTitle = itemView.findViewById(R.id.tvActivityTitle);
        TextView tvDesc = itemView.findViewById(R.id.tvActivityDesc);
        TextView tvTime = itemView.findViewById(R.id.tvActivityTime);

        icon.setImageResource(activity.iconRes);
        tvTitle.setText(activity.title);
        tvDesc.setText(activity.description);
        tvTime.setText(activity.time);

        binding.recentActivityContainer.addView(itemView);
    }

    private void setupQuickActions() {
        binding.btnBroadcast.setOnClickListener(v -> {
            adminLogRepository.logAction("BROADCAST_INITIATED", "All Users", "Opened broadcast dialog");
            Toast.makeText(getContext(), R.string.broadcast_hint, Toast.LENGTH_SHORT).show();
        });
        binding.btnManageUsers.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_adminDashboardFragment_to_userManagementFragment));
        binding.btnExportLogs.setOnClickListener(v -> {
            adminLogRepository.logAction("LOG_EXPORT", "System Logs", "Initiated log export");
            Navigation.findNavController(v).navigate(R.id.action_adminDashboardFragment_to_analyticsFragment);
        });
        binding.btnSystemCheck.setOnClickListener(v -> {
            adminLogRepository.logAction("SYSTEM_CHECK", "System", "Performed manual health check");
            Toast.makeText(getContext(), R.string.system_healthy, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupCardListeners() {
        binding.cardTotalUsers.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_adminDashboardFragment_to_userManagementFragment));
        
        binding.cardAiChats.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_adminDashboardFragment_to_chatMonitoringFragment));

        binding.cardOcrUsage.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_adminDashboardFragment_to_ocrMonitoringFragment));

        binding.cardGithubUsage.setOnClickListener(v ->
            Navigation.findNavController(v).navigate(R.id.action_adminDashboardFragment_to_githubMonitoringFragment));
    }

    private void setupBottomNav() {
        binding.adminBottomNav.setSelectedItemId(R.id.admin_nav_home);
        binding.adminBottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.admin_nav_home) {
                return true;
            } else if (id == R.id.admin_nav_logs) {
                Navigation.findNavController(requireView()).navigate(R.id.action_adminDashboardFragment_to_adminActivityLogFragment);
                return true;
            } else if (id == R.id.admin_nav_settings) {
                Navigation.findNavController(requireView()).navigate(R.id.action_adminDashboardFragment_to_adminSettingsFragment);
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
