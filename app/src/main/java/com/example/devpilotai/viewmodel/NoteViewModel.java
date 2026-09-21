package com.example.devpilotai.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.example.devpilotai.data.NoteRepository;
import com.example.devpilotai.data.model.Folder;
import com.example.devpilotai.data.model.Note;
import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private final NoteRepository repository;
    private final LiveData<List<Folder>> allFolders;
    
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<Integer> selectedFolderId = new MutableLiveData<>(null);
    
    private final MediatorLiveData<List<Note>> filteredNotes = new MediatorLiveData<>();

    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);
        allFolders = repository.getAllFolders();

        LiveData<List<Note>> source = Transformations.switchMap(selectedFolderId, folderId -> {
            String query = searchQuery.getValue();
            if (query != null && !query.isEmpty()) {
                return repository.searchNotes(query);
            } else if (folderId == null) {
                return repository.getAllNotes();
            } else {
                return repository.getNotesByFolder(folderId);
            }
        });

        filteredNotes.addSource(source, filteredNotes::setValue);
        filteredNotes.addSource(searchQuery, query -> {
            // Re-trigger the switchMap by poking selectedFolderId or similar
            selectedFolderId.setValue(selectedFolderId.getValue());
        });
    }

    public LiveData<List<Note>> getFilteredNotes() {
        return filteredNotes;
    }

    public LiveData<List<Folder>> getAllFolders() {
        return allFolders;
    }

    public void insertNote(Note note, NoteRepository.OnNoteInsertedCallback callback) {
        repository.insertNote(note, callback);
    }

    public void updateNote(Note note) {
        repository.updateNote(note);
    }

    public void deleteNote(Note note) {
        repository.deleteNote(note);
    }

    public void insertFolder(Folder folder) {
        repository.insertFolder(folder);
    }

    public void deleteFolder(Folder folder) {
        repository.deleteFolder(folder);
    }

    public LiveData<Note> getNoteById(int noteId) {
        return repository.getNoteById(noteId);
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public void setSelectedFolder(Integer folderId) {
        selectedFolderId.setValue(folderId);
    }
}
