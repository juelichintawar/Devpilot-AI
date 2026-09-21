package com.example.devpilotai.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.devpilotai.data.ChatLogRepository;
import com.example.devpilotai.data.GitHubLogRepository;
import com.example.devpilotai.data.OcrLogRepository;
import com.example.devpilotai.data.UserRepository;
import com.example.devpilotai.data.model.ChatLog;
import com.example.devpilotai.data.model.GitHubLog;
import com.example.devpilotai.data.model.OcrLog;
import com.example.devpilotai.data.model.User;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnalyticsViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final ChatLogRepository chatLogRepository;
    private final OcrLogRepository ocrLogRepository;
    private final GitHubLogRepository githubLogRepository;

    private final MutableLiveData<AnalyticsData> analyticsData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public AnalyticsViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository();
        chatLogRepository = new ChatLogRepository();
        ocrLogRepository = new OcrLogRepository();
        githubLogRepository = new GitHubLogRepository();
        loadAnalytics();
    }

    public void loadAnalytics() {
        isLoading.setValue(true);
        userRepository.getAllUsers(new UserRepository.UsersCallback() {
            @Override
            public void onSuccess(List<User> users) {
                fetchChatLogs(users);
            }

            @Override
            public void onFailure(String message) {
                error.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    private void fetchChatLogs(List<User> users) {
        chatLogRepository.getAllLogs(new ChatLogRepository.LogsCallback() {
            @Override
            public void onSuccess(List<ChatLog> chatLogs) {
                fetchOcrLogs(users, chatLogs);
            }

            @Override
            public void onFailure(String message) {
                error.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    private void fetchOcrLogs(List<User> users, List<ChatLog> chatLogs) {
        ocrLogRepository.getAllLogs(new OcrLogRepository.LogsCallback() {
            @Override
            public void onSuccess(List<OcrLog> ocrLogs) {
                fetchGithubLogs(users, chatLogs, ocrLogs);
            }

            @Override
            public void onFailure(String message) {
                error.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    private void fetchGithubLogs(List<User> users, List<ChatLog> chatLogs, List<OcrLog> ocrLogs) {
        githubLogRepository.getAllLogs(new GitHubLogRepository.LogsCallback() {
            @Override
            public void onSuccess(List<GitHubLog> githubLogs) {
                processData(users, chatLogs, ocrLogs, githubLogs);
            }

            @Override
            public void onFailure(String message) {
                error.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    private void processData(List<User> users, List<ChatLog> chatLogs, List<OcrLog> ocrLogs, List<GitHubLog> githubLogs) {
        AnalyticsData data = new AnalyticsData();
        data.totalUsers = users.size();
        data.aiRequests = chatLogs.size();
        data.ocrScans = ocrLogs.size();
        data.githubActions = githubLogs.size();

        // Daily Active Users (DAU) - Users who had any activity in the last 24 hours
        long oneDayAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000);
        Set<String> activeUserEmails = new HashSet<>();
        
        int failedAi = 0;
        for (ChatLog log : chatLogs) {
            if (log.getTimestamp() > oneDayAgo) activeUserEmails.add(log.getUserEmail());
            if ("FAILED".equals(log.getStatus())) failedAi++;
        }

        int failedOcr = 0;
        for (OcrLog log : ocrLogs) {
            if (log.getTimestamp() > oneDayAgo) activeUserEmails.add(log.getUserEmail());
            if ("FAILED".equals(log.getStatus())) failedOcr++;
        }

        int failedGithub = 0;
        for (GitHubLog log : githubLogs) {
            if (log.getTimestamp() > oneDayAgo) activeUserEmails.add(log.getUserEmail());
            if ("FAILED".equals(log.getStatus())) failedGithub++;
        }

        data.dailyActiveUsers = activeUserEmails.size();
        data.failedRequests = failedAi + failedOcr + failedGithub;

        // Most used module
        Map<String, Integer> usage = new HashMap<>();
        usage.put("AI Chat", data.aiRequests);
        usage.put("OCR", data.ocrScans);
        usage.put("GitHub", data.githubActions);
        
        String mostUsed = "None";
        int maxUsage = -1;
        for (Map.Entry<String, Integer> entry : usage.entrySet()) {
            if (entry.getValue() > maxUsage) {
                maxUsage = entry.getValue();
                mostUsed = entry.getKey();
            }
        }
        data.mostUsedModule = mostUsed;

        // Chart Data Generation (Last 7 days)
        data.activityChartData = generateLast7DaysActivity(chatLogs, ocrLogs, githubLogs);

        analyticsData.postValue(data);
        isLoading.postValue(false);
    }

    private Map<String, Integer> generateLast7DaysActivity(List<ChatLog> chatLogs, List<OcrLog> ocrLogs, List<GitHubLog> githubLogs) {
        Map<String, Integer> last7Days = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            String label = getDayLabel(cal);
            last7Days.put(label, 0);
            
            long startOfDay = getStartOfDay(cal);
            long endOfDay = getEndOfDay(cal);

            int count = 0;
            for (ChatLog log : chatLogs) if (log.getTimestamp() >= startOfDay && log.getTimestamp() <= endOfDay) count++;
            for (OcrLog log : ocrLogs) if (log.getTimestamp() >= startOfDay && log.getTimestamp() <= endOfDay) count++;
            for (GitHubLog log : githubLogs) if (log.getTimestamp() >= startOfDay && log.getTimestamp() <= endOfDay) count++;
            
            last7Days.put(label, count);
            cal.add(Calendar.DAY_OF_YEAR, -1);
        }
        return last7Days;
    }

    private String getDayLabel(Calendar cal) {
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        return days[cal.get(Calendar.DAY_OF_WEEK) - 1];
    }

    private long getStartOfDay(Calendar cal) {
        Calendar c = (Calendar) cal.clone();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    private long getEndOfDay(Calendar cal) {
        Calendar c = (Calendar) cal.clone();
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTimeInMillis();
    }

    public LiveData<AnalyticsData> getAnalyticsData() { return analyticsData; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getError() { return error; }

    public static class AnalyticsData {
        public int dailyActiveUsers;
        public int totalUsers;
        public int aiRequests;
        public int ocrScans;
        public int githubActions;
        public String mostUsedModule;
        public int failedRequests;
        public Map<String, Integer> activityChartData;
    }
}
