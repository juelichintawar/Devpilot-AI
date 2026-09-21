package com.example.devpilotai.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.devpilotai.data.OcrLogRepository;
import com.example.devpilotai.data.model.OcrLog;

import java.util.List;

public class OcrMonitoringViewModel extends AndroidViewModel {
    private final OcrLogRepository repository;
    private final MutableLiveData<List<OcrLog>> logs = new MutableLiveData<>();
    private final MutableLiveData<OcrStats> stats = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    public OcrMonitoringViewModel(@NonNull Application application) {
        super(application);
        repository = new OcrLogRepository();
        loadLogs();
    }

    public void loadLogs() {
        loading.setValue(true);
        repository.getAllLogs(new OcrLogRepository.LogsCallback() {
            @Override
            public void onSuccess(List<OcrLog> logList) {
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

    private void calculateStats(List<OcrLog> logList) {
        int total = logList.size();
        int success = 0;
        int failed = 0;
        for (OcrLog log : logList) {
            if ("SUCCESS".equalsIgnoreCase(log.getStatus())) {
                success++;
            } else {
                failed++;
            }
        }
        stats.postValue(new OcrStats(total, success, failed));
    }

    public LiveData<List<OcrLog>> getLogs() { return logs; }
    public LiveData<OcrStats> getStats() { return stats; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getLoading() { return loading; }

    public static class OcrStats {
        public final int totalScans;
        public final int successfulScans;
        public final int failedScans;

        public OcrStats(int totalScans, int successfulScans, int failedScans) {
            this.totalScans = totalScans;
            this.successfulScans = successfulScans;
            this.failedScans = failedScans;
        }
    }
}
