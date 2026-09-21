package com.example.devpilotai.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.devpilotai.data.SnippetRepository;
import com.example.devpilotai.data.model.Snippet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SnippetViewModel extends AndroidViewModel {

    private final SnippetRepository repository;
    
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<String> categoryFilter = new MutableLiveData<>("All");
    private final MutableLiveData<SortOrder> sortOrder = new MutableLiveData<>(SortOrder.NEWEST);
    
    private final MediatorLiveData<List<Snippet>> filteredSnippets = new MediatorLiveData<>();
    private final MediatorLiveData<List<String>> categories = new MediatorLiveData<>();

    public enum SortOrder {
        NEWEST, OLDEST, TITLE_AZ, TITLE_ZA
    }

    public SnippetViewModel(@NonNull Application application) {
        super(application);
        repository = new SnippetRepository(application);
        
        LiveData<List<Snippet>> allSnippets = repository.getAllSnippets();
        
        filteredSnippets.addSource(allSnippets, snippets -> combineFilters(snippets, searchQuery.getValue(), categoryFilter.getValue(), sortOrder.getValue()));
        filteredSnippets.addSource(searchQuery, query -> combineFilters(allSnippets.getValue(), query, categoryFilter.getValue(), sortOrder.getValue()));
        filteredSnippets.addSource(categoryFilter, category -> combineFilters(allSnippets.getValue(), searchQuery.getValue(), category, sortOrder.getValue()));
        filteredSnippets.addSource(sortOrder, order -> combineFilters(allSnippets.getValue(), searchQuery.getValue(), categoryFilter.getValue(), order));

        categories.addSource(allSnippets, snippets -> {
            Set<String> categorySet = new HashSet<>();
            categorySet.add("All");
            categorySet.add("Favorites");
            if (snippets != null) {
                for (Snippet snippet : snippets) {
                    if (snippet.getCategory() != null && !snippet.getCategory().isEmpty()) {
                        categorySet.add(snippet.getCategory());
                    }
                }
            }
            List<String> sortedCategories = new ArrayList<>(categorySet);
            Collections.sort(sortedCategories);
            categories.setValue(sortedCategories);
        });
    }

    private void combineFilters(List<Snippet> snippets, String query, String category, SortOrder order) {
        if (snippets == null) {
            filteredSnippets.setValue(new ArrayList<>());
            return;
        }

        List<Snippet> result = new ArrayList<>();
        for (Snippet snippet : snippets) {
            boolean matchesSearch = query.isEmpty() || 
                    snippet.getTitle().toLowerCase().contains(query.toLowerCase()) || 
                    snippet.getContent().toLowerCase().contains(query.toLowerCase()) ||
                    (snippet.getTags() != null && snippet.getTags().toLowerCase().contains(query.toLowerCase()));
            
            boolean matchesCategory = category.equals("All") || 
                    (category.equals("Favorites") && snippet.isFavorite()) ||
                    (snippet.getCategory() != null && snippet.getCategory().equals(category));

            if (matchesSearch && matchesCategory) {
                result.add(snippet);
            }
        }

        // Apply Sorting
        Collections.sort(result, (s1, s2) -> {
            switch (order) {
                case NEWEST:
                    return Long.compare(s2.getCreatedAt(), s1.getCreatedAt());
                case OLDEST:
                    return Long.compare(s1.getCreatedAt(), s2.getCreatedAt());
                case TITLE_AZ:
                    return s1.getTitle().compareToIgnoreCase(s2.getTitle());
                case TITLE_ZA:
                    return s2.getTitle().compareToIgnoreCase(s1.getTitle());
                default:
                    return 0;
            }
        });

        filteredSnippets.setValue(result);
    }

    public void insert(Snippet snippet) {
        repository.insert(snippet);
    }

    public void update(Snippet snippet) {
        repository.update(snippet);
    }

    public void delete(Snippet snippet) {
        repository.delete(snippet);
    }

    public LiveData<List<Snippet>> getSnippets() {
        return filteredSnippets;
    }

    public LiveData<List<String>> getCategories() {
        return categories;
    }

    public LiveData<List<Snippet>> getAllSnippets() {
        return repository.getAllSnippets();
    }

    public LiveData<Snippet> getSnippetById(int id) {
        return repository.getSnippetById(id);
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public void setCategoryFilter(String category) {
        categoryFilter.setValue(category);
    }

    public void setSortOrder(SortOrder order) {
        sortOrder.setValue(order);
    }
}
