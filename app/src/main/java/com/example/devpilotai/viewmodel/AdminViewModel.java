package com.example.devpilotai.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.devpilotai.R;
import com.example.devpilotai.data.AdminLogRepository;
import com.example.devpilotai.data.model.AdminLog;

import java.util.ArrayList;
import java.util.List;

public class AdminViewModel extends AndroidViewModel {

    private final MutableLiveData<AdminStats> stats = new MutableLiveData<>();
    private final MutableLiveData<List<AdminActivity>> recentActivities = new MutableLiveData<>();
    private final AdminLogRepository adminLogRepository;

    public AdminViewModel(@NonNull Application application) {
        super(application);
        adminLogRepository = new AdminLogRepository();
        loadStats();
        loadRecentActivities();
    }

    private void loadStats() {
        // In a real app, these would be fetched from a repository/API
        stats.setValue(new AdminStats("1,284", "452", "8,421", "2,105", "562", "3,412"));
    }

    public void loadRecentActivities() {
        adminLogRepository.getAllLogs(new AdminLogRepository.LogsCallback() {
            @Override
            public void onSuccess(List<AdminLog> logs) {
                List<AdminActivity> activities = new ArrayList<>();
                // Take top 5 for dashboard
                int count = Math.min(logs.size(), 5);
                for (int i = 0; i < count; i++) {
                    AdminLog log = logs.get(i);
                    activities.add(new AdminActivity(
                            log.getAction(),
                            log.getTarget(),
                            formatTime(log.getTimestamp()),
                            getIconForAction(log.getAction())
                    ));
                }
                recentActivities.postValue(activities);
            }

            @Override
            public void onFailure(String message) {
                // Silently fail for dashboard background load or handle error
            }
        });
    }

    private String formatTime(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        long minutes = diff / (1000 * 60);
        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + " mins ago";
        long hours = minutes / 60;
        if (hours < 24) return hours + " hours ago";
        return (hours / 24) + " days ago";
    }

    private int getIconForAction(String action) {
        if (action == null) return R.drawable.ic_settings;
        switch (action) {
            case "ADMIN_LOGIN": return R.drawable.ic_users; // Using ic_users as ic_profile is missing
            case "USER_STATUS_CHANGE":
            case "USER_DELETION": return R.drawable.ic_users;
            case "SETTINGS_CHANGE": return R.drawable.ic_settings;
            default: return R.drawable.ic_settings;
        }
    }

    public LiveData<AdminStats> getStats() {
        return stats;
    }

    public LiveData<List<AdminActivity>> getRecentActivities() {
        return recentActivities;
    }

    public static class AdminStats {
        public final String totalUsers;
        public final String activeUsers;
        public final String totalChats;
        public final String ocrUsage;
        public final String githubUsage;
        public final String snippetsCount;

        public AdminStats(String totalUsers, String activeUsers, String totalChats, String ocrUsage, String githubUsage, String snippetsCount) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.totalChats = totalChats;
            this.ocrUsage = ocrUsage;
            this.githubUsage = githubUsage;
            this.snippetsCount = snippetsCount;
        }
    }

    public static class AdminActivity {
        public final String title;
        public final String description;
        public final String time;
        public final int iconRes;

        public AdminActivity(String title, String description, String time, int iconRes) {
            this.title = title;
            this.description = description;
            this.time = time;
            this.iconRes = iconRes;
        }
    }
}
