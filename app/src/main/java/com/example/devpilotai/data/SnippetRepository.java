package com.example.devpilotai.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.devpilotai.data.model.Snippet;
import com.example.devpilotai.data.model.SnippetDao;
import com.example.devpilotai.data.model.UserStatsDao;
import com.example.devpilotai.utils.AppExecutors;

import java.util.List;

/**
 * Repository for managing Snippet data using Room database.
 * Uses AppExecutors for thread management.
 */
public class SnippetRepository {
    private final SnippetDao snippetDao;
    private final UserStatsDao userStatsDao;
    private final LiveData<List<Snippet>> allSnippets;
    private final AppExecutors appExecutors;

    public SnippetRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        snippetDao = db.snippetDao();
        userStatsDao = db.userStatsDao();
        allSnippets = snippetDao.getAllSnippets();
        appExecutors = AppExecutors.getInstance();
    }

    public void insert(Snippet snippet) {
        appExecutors.diskIO().execute(() -> {
            snippetDao.insert(snippet);
            userStatsDao.incrementSnippets();
        });
    }

    public void update(Snippet snippet) {
        appExecutors.diskIO().execute(() -> snippetDao.update(snippet));
    }

    public void delete(Snippet snippet) {
        appExecutors.diskIO().execute(() -> snippetDao.delete(snippet));
    }

    public LiveData<List<Snippet>> getAllSnippets() {
        return allSnippets;
    }

    public LiveData<List<Snippet>> getFavoriteSnippets() {
        return snippetDao.getFavoriteSnippets();
    }

    public LiveData<List<Snippet>> getSnippetsByCategory(String category) {
        return snippetDao.getSnippetsByCategory(category);
    }

    public LiveData<List<Snippet>> searchSnippets(String query) {
        return snippetDao.searchSnippets("%" + query + "%");
    }

    public LiveData<Snippet> getSnippetById(int id) {
        return snippetDao.getSnippetById(id);
    }
}
