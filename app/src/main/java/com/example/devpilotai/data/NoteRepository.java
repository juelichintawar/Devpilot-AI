package com.example.devpilotai.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.devpilotai.data.model.Folder;
import com.example.devpilotai.data.model.FolderDao;
import com.example.devpilotai.data.model.Note;
import com.example.devpilotai.data.model.NoteDao;
import com.example.devpilotai.data.model.UserStatsDao;
import com.example.devpilotai.utils.AppExecutors;
import java.util.List;

/**
 * Repository for managing Note and Folder data using Room database.
 * Uses AppExecutors for background thread operations.
 */
public class NoteRepository {
    private final NoteDao noteDao;
    private final FolderDao folderDao;
    private final UserStatsDao userStatsDao;
    private final AppExecutors appExecutors;

    public NoteRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        noteDao = db.noteDao();
        folderDao = db.folderDao();
        userStatsDao = db.userStatsDao();
        appExecutors = AppExecutors.getInstance();
    }

    public LiveData<List<Note>> getAllNotes() {
        return noteDao.getAllNotes();
    }

    public LiveData<List<Note>> getNotesByFolder(int folderId) {
        return noteDao.getNotesByFolder(folderId);
    }

    public LiveData<Note> getNoteById(int noteId) {
        return noteDao.getNoteById(noteId);
    }

    public LiveData<List<Note>> searchNotes(String query) {
        return noteDao.searchNotes(query);
    }

    public void insertNote(Note note, OnNoteInsertedCallback callback) {
        appExecutors.diskIO().execute(() -> {
            long id = noteDao.insert(note);
            userStatsDao.incrementNotes();
            if (callback != null) {
                appExecutors.mainThread().execute(() -> callback.onInserted((int) id));
            }
        });
    }

    public void updateNote(Note note) {
        appExecutors.diskIO().execute(() -> noteDao.update(note));
    }

    public void deleteNote(Note note) {
        appExecutors.diskIO().execute(() -> noteDao.delete(note));
    }

    public LiveData<List<Folder>> getAllFolders() {
        return folderDao.getAllFolders();
    }

    public void insertFolder(Folder folder) {
        appExecutors.diskIO().execute(() -> folderDao.insert(folder));
    }

    public void deleteFolder(Folder folder) {
        appExecutors.diskIO().execute(() -> folderDao.delete(folder));
    }

    /**
     * Callback for asynchronous note insertion.
     */
    public interface OnNoteInsertedCallback {
        void onInserted(int noteId);
    }
}
