package com.example.devpilotai.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.devpilotai.data.ChatLogRepository;
import com.example.devpilotai.data.model.ChatLog;

import java.util.List;

public class ChatMonitoringViewModel extends AndroidViewModel {
    private final ChatLogRepository repository;
    private final MutableLiveData<List<ChatLog>> logs = new MutableLiveData<>();
    private final MutableLiveData<ChatStats> stats = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    public ChatMonitoringViewModel(@NonNull Application application) {
        super(application);
        repository = new ChatLogRepository();
        loadLogs();
    }

    public void loadLogs() {
        loading.setValue(true);
        repository.getAllLogs(new ChatLogRepository.LogsCallback() {
            @Override
            public void onSuccess(List<ChatLog> logList) {
                logs.postValue(logList);
                calculateStats(logList);
                loading.postValue(false);
            }

            @Override
            public void onFailure(String message) {
                error.postValue(message);
                loading.postValue(false);
            }
        });
    }

    private void calculateStats(List<ChatLog> logList) {
        int total = logList.size();
        int success = 0;
        int failed = 0;
        for (ChatLog log : logList) {
            if ("SUCCESS".equalsIgnoreCase(log.getStatus())) {
                success++;
            } else {
                failed++;
            }
        }
        stats.postValue(new ChatStats(total, success, failed));
    }

    public LiveData<List<ChatLog>> getLogs() { return logs; }
    public LiveData<ChatStats> getStats() { return stats; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getLoading() { return loading; }

    public static class ChatStats {
        public final int totalRequests;
        public final int successfulRequests;
        public final int failedRequests;

        public ChatStats(int totalRequests, int successfulRequests, int failedRequests) {
            this.totalRequests = totalRequests;
            this.successfulRequests = successfulRequests;
            this.failedRequests = failedRequests;
        }
    }
}
