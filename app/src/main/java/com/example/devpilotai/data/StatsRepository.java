package com.example.devpilotai.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.devpilotai.data.model.UserStats;
import com.example.devpilotai.data.model.UserStatsDao;
import com.example.devpilotai.utils.AppExecutors;

public class StatsRepository {
    private final UserStatsDao userStatsDao;
    private final AppExecutors appExecutors;

    public StatsRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        userStatsDao = db.userStatsDao();
        appExecutors = AppExecutors.getInstance();
        
        // Ensure initial row exists
        appExecutors.diskIO().execute(() -> {
            if (userStatsDao.getUserStatsSync() == null) {
                userStatsDao.insert(new UserStats());
            }
        });
    }

    public LiveData<UserStats> getUserStats() {
        return userStatsDao.getUserStats();
    }

    public void incrementTokens(int tokens) {
        appExecutors.diskIO().execute(() -> userStatsDao.incrementTokens(tokens));
    }

    public void incrementSnippets() {
        appExecutors.diskIO().execute(userStatsDao::incrementSnippets);
    }

    public void incrementOcrScans() {
        appExecutors.diskIO().execute(userStatsDao::incrementOcrScans);
    }

    public void incrementNotes() {
        appExecutors.diskIO().execute(userStatsDao::incrementNotes);
    }
}
