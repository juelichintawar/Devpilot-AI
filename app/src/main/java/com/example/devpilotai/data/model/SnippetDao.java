package com.example.devpilotai.data.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SnippetDao {
    @Insert
    void insert(Snippet snippet);

    @Update
    void update(Snippet snippet);

    @Delete
    void delete(Snippet snippet);

    @Query("SELECT * FROM snippets ORDER BY createdAt DESC")
    LiveData<List<Snippet>> getAllSnippets();

    @Query("SELECT * FROM snippets WHERE id = :id")
    LiveData<Snippet> getSnippetById(int id);

    @Query("SELECT * FROM snippets WHERE isFavorite = 1 ORDER BY createdAt DESC")
    LiveData<List<Snippet>> getFavoriteSnippets();

    @Query("SELECT * FROM snippets WHERE category = :category ORDER BY createdAt DESC")
    LiveData<List<Snippet>> getSnippetsByCategory(String category);

    @Query("SELECT * FROM snippets WHERE title LIKE :searchQuery OR content LIKE :searchQuery OR tags LIKE :searchQuery")
    LiveData<List<Snippet>> searchSnippets(String searchQuery);
}
