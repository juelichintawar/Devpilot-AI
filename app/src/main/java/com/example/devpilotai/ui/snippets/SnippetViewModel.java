package com.example.devpilotai.ui.snippets;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.devpilotai.data.SnippetRepository;
import com.example.devpilotai.data.model.Snippet;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SnippetViewModel extends AndroidViewModel {
    private final SnippetRepository repository;

    public enum SortOrder {
        NEWEST, OLDEST, TITLE_AZ, TITLE_ZA
    }

    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<String> categoryFilter = new MutableLiveData<>("All");
    private final MutableLiveData<SortOrder> sortOrder = new MutableLiveData<>(SortOrder.NEWEST);

    private final LiveData<List<Snippet>> snippets;

    public SnippetViewModel(@NonNull Application application) {
        super(application);
        repository = new SnippetRepository(application);

        // In a real-world app with lots of data, we'd do this filtering in the DAO with @RawQuery or a complex @Query.
        // For this implementation, we'll use switchMap on the search query and then apply further filtering/sorting in the ViewModel.
        
        LiveData<List<Snippet>> rawSnippets = Transformations.switchMap(searchQuery, query -> {
            if (query == null || query.isEmpty()) {
                return repository.getAllSnippets();
            } else {
                return repository.searchSnippets(query);
            }
        });

        snippets = Transformations.map(rawSnippets, list -> {
            if (list == null) return null;

            // Apply category filter
            String category = categoryFilter.getValue();
            List<Snippet> filteredList = list;
            if (category != null && !category.equals("All")) {
                if (category.equals("Favorites")) {
                    filteredList = list.stream().filter(Snippet::isFavorite).collect(Collectors.toList());
                } else {
                    filteredList = list.stream().filter(s -> category.equals(s.getCategory())).collect(Collectors.toList());
                }
            }

            // Apply sorting
            SortOrder order = sortOrder.getValue();
            if (order != null) {
                switch (order) {
                    case NEWEST:
                        filteredList.sort((s1, s2) -> Long.compare(s2.getCreatedAt(), s1.getCreatedAt()));
                        break;
                    case OLDEST:
                        filteredList.sort((s1, s2) -> Long.compare(s1.getCreatedAt(), s2.getCreatedAt()));
                        break;
                    case TITLE_AZ:
                        filteredList.sort((s1, s2) -> s1.getTitle().compareToIgnoreCase(s2.getTitle()));
                        break;
                    case TITLE_ZA:
                        filteredList.sort((s1, s2) -> s2.getTitle().compareToIgnoreCase(s1.getTitle()));
                        break;
                }
            }
            return filteredList;
        });
    }

    public LiveData<List<Snippet>> getSnippets() {
        return snippets;
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

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public void setCategoryFilter(String category) {
        categoryFilter.setValue(category);
        // Trigger a refresh of the snippets transformation
        searchQuery.setValue(searchQuery.getValue());
    }

    public void setSortOrder(SortOrder order) {
        sortOrder.setValue(order);
        // Trigger a refresh
        searchQuery.setValue(searchQuery.getValue());
    }

    public LiveData<Snippet> getSnippetById(int id) {
        // We'll add this to repository if not exists, or just use Dao directly via repository
        return repository.getSnippetById(id);
    }
}
