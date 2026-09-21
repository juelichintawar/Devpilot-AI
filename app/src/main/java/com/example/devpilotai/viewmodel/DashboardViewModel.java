package com.example.devpilotai.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.devpilotai.R;
import com.example.devpilotai.data.QuickAction;
import com.example.devpilotai.data.RecentActivity;
import com.example.devpilotai.data.StatsRepository;
import com.example.devpilotai.data.model.UserStats;
import com.example.devpilotai.utils.AppExecutors;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for the Dashboard.
 * Manages UI state and real-time statistics for the application.
 */
public class DashboardViewModel extends AndroidViewModel {

    private final MutableLiveData<List<QuickAction>> quickActions = new MutableLiveData<>();
    private final MutableLiveData<List<RecentActivity>> recentActivities = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final StatsRepository statsRepository;
    private final AppExecutors appExecutors;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        appExecutors = AppExecutors.getInstance();
        statsRepository = new StatsRepository(application);
    }

    public LiveData<List<QuickAction>> getQuickActions() {
        return quickActions;
    }

    public LiveData<List<RecentActivity>> getRecentActivities() {
        return recentActivities;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<UserStats> getUserStats() {
        return statsRepository.getUserStats();
    }

    /**
     * Loads dashboard data.
     */
    public void loadDashboardData() {
        if (isLoading.getValue() != null && isLoading.getValue()) return;

        isLoading.setValue(true);
        
        // Simulate background data fetch for non-DB content
        appExecutors.networkIO().execute(() -> {
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            appExecutors.mainThread().execute(() -> {
                quickActions.setValue(createQuickActions());
                recentActivities.setValue(createRecentActivities());
                isLoading.setValue(false);
            });
        });
    }

    private List<QuickAction> createQuickActions() {
        List<QuickAction> actions = new ArrayList<>();
        actions.add(new QuickAction(getApplication().getString(R.string.ai_chat), android.R.drawable.ic_menu_send, R.color.card_bg_1));
        actions.add(new QuickAction(getApplication().getString(R.string.ocr_scanner), android.R.drawable.ic_menu_camera, R.color.card_bg_2));
        actions.add(new QuickAction(getApplication().getString(R.string.snippets), android.R.drawable.ic_menu_edit, R.color.card_bg_3));
        actions.add(new QuickAction(getApplication().getString(R.string.github), android.R.drawable.ic_menu_share, R.color.card_bg_4));
        actions.add(new QuickAction(getApplication().getString(R.string.notes), android.R.drawable.ic_menu_sort_alphabetically, R.color.card_bg_1));
        actions.add(new QuickAction(getApplication().getString(R.string.challenges), android.R.drawable.ic_menu_help, R.color.card_bg_2));
        actions.add(new QuickAction(getApplication().getString(R.string.profile), android.R.drawable.ic_menu_myplaces, R.color.card_bg_3));
        actions.add(new QuickAction(getApplication().getString(R.string.history), android.R.drawable.ic_menu_recent_history, R.color.card_bg_4));
        return actions;
    }

    private List<RecentActivity> createRecentActivities() {
        List<RecentActivity> activities = new ArrayList<>();
        activities.add(new RecentActivity("Refactored UserModule.java", "Code Snippet saved to Library", "2h ago", 
                android.R.drawable.ic_menu_edit, ContextCompat.getColor(getApplication(), R.color.accent_blue), 
                ContextCompat.getColor(getApplication(), R.color.card_bg_1)));
        
        activities.add(new RecentActivity("OCR Scan: Receipt.jpg", "Extracted 12 lines of text", "5h ago", 
                android.R.drawable.ic_menu_camera, ContextCompat.getColor(getApplication(), R.color.accent_purple), 
                ContextCompat.getColor(getApplication(), R.color.card_bg_2)));
        return activities;
    }
}
