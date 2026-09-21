package com.example.devpilotai.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.devpilotai.data.GitHubLogRepository;
import com.example.devpilotai.data.UserRepository;
import com.example.devpilotai.data.model.GitHubLog;
import com.example.devpilotai.data.model.User;

import java.util.List;

public class GitHubMonitoringViewModel extends AndroidViewModel {
    private final GitHubLogRepository logRepository;
    private final UserRepository userRepository;
    
    private final MutableLiveData<List<GitHubLog>> logs = new MutableLiveData<>();
    private final MutableLiveData<GitHubStats> stats = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    public GitHubMonitoringViewModel(@NonNull Application application) {
        super(application);
        logRepository = new GitHubLogRepository();
        userRepository = new UserRepository();
        loadData();
    }

    public void loadData() {
        loading.setValue(true);
        userRepository.getAllUsers(new UserRepository.UsersCallback() {
            @Override
            public void onSuccess(List<User> users) {
                int connectedCount = 0;
                for (User user : users) {
                    if (user.isGitHubConnected()) {
                        connectedCount++;
                    }
                }
                final int finalConnectedCount = connectedCount;
                
                logRepository.getAllLogs(new GitHubLogRepository.LogsCallback() {
                    @Override
                    public void onSuccess(List<GitHubLog> logList) {
                        logs.postValue(logList);
                        calculateStats(logList, finalConnectedCount);
                        loading.postValue(false);
                    }

                    @Override
                    public void onFailure(String message) {
                        error.postValue(message);
                        loading.postValue(false);
                    }
                });
            }

            @Override
            public void onFailure(String message) {
                error.postValue(message);
                loading.postValue(false);
            }
        });
    }

    private void calculateStats(List<GitHubLog> logList, int connectedUsers) {
        int totalActions = logList.size();
        int successfulActions = 0;
        int failedActions = 0;
        long lastSync = 0;

        for (GitHubLog log : logList) {
            if ("SUCCESS".equalsIgnoreCase(log.getStatus())) {
                successfulActions++;
            } else {
                failedActions++;
            }
            if (log.getTimestamp() > lastSync) {
                lastSync = log.getTimestamp();
            }
        }
        stats.postValue(new GitHubStats(connectedUsers, totalActions, successfulActions, failedActions, lastSync));
    }

    public LiveData<List<GitHubLog>> getLogs() { return logs; }
    public LiveData<GitHubStats> getStats() { return stats; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getLoading() { return loading; }

    public static class GitHubStats {
        public final int connectedUsers;
        public final int totalActions;
        public final int successfulActions;
        public final int failedActions;
        public final long lastSyncTimestamp;

        public GitHubStats(int connectedUsers, int totalActions, int successfulActions, int failedActions, long lastSyncTimestamp) {
            this.connectedUsers = connectedUsers;
            this.totalActions = totalActions;
            this.successfulActions = successfulActions;
            this.failedActions = failedActions;
            this.lastSyncTimestamp = lastSyncTimestamp;
        }
    }
}
