package com.example.devpilotai.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.devpilotai.data.AdminLogRepository;
import com.example.devpilotai.data.model.AdminLog;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdminLogViewModel extends AndroidViewModel {
    private final AdminLogRepository repository;
    private final MutableLiveData<List<AdminLog>> allLogs = new MutableLiveData<>();
    private final MutableLiveData<List<AdminLog>> filteredLogs = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    public AdminLogViewModel(@NonNull Application application) {
        super(application);
        repository = new AdminLogRepository();
        loadLogs();
    }

    public void loadLogs() {
        loading.setValue(true);
        repository.getAllLogs(new AdminLogRepository.LogsCallback() {
            @Override
            public void onSuccess(List<AdminLog> logs) {
                allLogs.postValue(logs);
                filteredLogs.postValue(logs);
                loading.postValue(false);
            }

            @Override
            public void onFailure(String message) {
                error.postValue(message);
                loading.postValue(false);
            }
        });
    }

    public void filterLogs(String query) {
        List<AdminLog> currentAll = allLogs.getValue();
        if (currentAll == null) return;

        if (query == null || query.isEmpty()) {
            filteredLogs.setValue(currentAll);
        } else {
            String lowerQuery = query.toLowerCase();
            List<AdminLog> filtered = currentAll.stream()
                .filter(log -> (log.getAction() != null && log.getAction().toLowerCase().contains(lowerQuery)) ||
                             (log.getAdminEmail() != null && log.getAdminEmail().toLowerCase().contains(lowerQuery)) ||
                             (log.getDetails() != null && log.getDetails().toLowerCase().contains(lowerQuery)) ||
                             (log.getTarget() != null && log.getTarget().toLowerCase().contains(lowerQuery)))
                .collect(Collectors.toList());
            filteredLogs.setValue(filtered);
        }
    }

    public LiveData<List<AdminLog>> getFilteredLogs() { return filteredLogs; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getLoading() { return loading; }
}
