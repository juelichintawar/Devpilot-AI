package com.example.devpilotai.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.devpilotai.data.ChatMessageRepository;
import com.example.devpilotai.data.GitHubRepository;
import com.example.devpilotai.data.NoteRepository;
import com.example.devpilotai.data.SnippetRepository;
import com.example.devpilotai.data.model.ChatMessage;
import com.example.devpilotai.data.model.GitHubRepo;
import com.example.devpilotai.data.model.Note;
import com.example.devpilotai.data.model.SearchResult;
import com.example.devpilotai.data.model.Snippet;

import java.util.ArrayList;
import java.util.List;

public class GlobalSearchViewModel extends AndroidViewModel {

    private final SnippetRepository snippetRepository;
    private final NoteRepository noteRepository;
    private final ChatMessageRepository chatRepository;
    private final GitHubRepository githubRepository;

    private final MutableLiveData<String> query = new MutableLiveData<>("");
    private final MediatorLiveData<List<SearchResult>> searchResults = new MediatorLiveData<>();

    private LiveData<List<Snippet>> snippetSource;
    private LiveData<List<Note>> noteSource;
    private LiveData<List<ChatMessage>> chatSource;
    private LiveData<List<GitHubRepo>> repoSource;

    public GlobalSearchViewModel(@NonNull Application application) {
        super(application);
        snippetRepository = new SnippetRepository(application);
        noteRepository = new NoteRepository(application);
        chatRepository = new ChatMessageRepository(application);
        githubRepository = new GitHubRepository();

        setupSearch();
    }

    private void setupSearch() {
        searchResults.addSource(query, q -> {
            if (q == null || q.trim().isEmpty()) {
                searchResults.setValue(new ArrayList<>());
                return;
            }

            // Remove old sources
            if (snippetSource != null) searchResults.removeSource(snippetSource);
            if (noteSource != null) searchResults.removeSource(noteSource);
            if (chatSource != null) searchResults.removeSource(chatSource);
            if (repoSource != null) searchResults.removeSource(repoSource);

            // New sources based on query
            snippetSource = snippetRepository.searchSnippets(q);
            noteSource = noteRepository.searchNotes(q);
            chatSource = chatRepository.searchChatHistory(q);
            repoSource = githubRepository.searchRepositories(q);

            searchResults.addSource(snippetSource, list -> combineResults());
            searchResults.addSource(noteSource, list -> combineResults());
            searchResults.addSource(chatSource, list -> combineResults());
            searchResults.addSource(repoSource, list -> combineResults());
        });
    }

    private void combineResults() {
        List<SearchResult> all = new ArrayList<>();
        String currentQuery = query.getValue();
        
        if (snippetSource != null && snippetSource.getValue() != null) {
            for (Snippet s : snippetSource.getValue()) {
                all.add(new SearchResult(String.valueOf(s.getId()), s.getTitle(), s.getContent(), SearchResult.Type.SNIPPET, s));
            }
        }
        if (noteSource != null && noteSource.getValue() != null) {
            for (Note n : noteSource.getValue()) {
                all.add(new SearchResult(String.valueOf(n.getId()), n.getTitle(), n.getContent(), SearchResult.Type.NOTE, n));
            }
        }
        if (chatSource != null && chatSource.getValue() != null) {
            for (ChatMessage c : chatSource.getValue()) {
                all.add(new SearchResult(String.valueOf(c.getId()), c.getType() == ChatMessage.TYPE_USER ? "You" : "DevPilot AI", c.getContent(), SearchResult.Type.CHAT, c));
            }
        }
        if (repoSource != null && repoSource.getValue() != null) {
            for (GitHubRepo r : repoSource.getValue()) {
                all.add(new SearchResult(String.valueOf(r.getId()), r.getFullName(), r.getDescription(), SearchResult.Type.REPOSITORY, r));
            }
        }
        
        searchResults.setValue(all);
    }

    public void setQuery(String q) {
        query.setValue(q);
    }

    public LiveData<List<SearchResult>> getSearchResults() {
        return searchResults;
    }
    
    public String getQuery() {
        return query.getValue();
    }
}
