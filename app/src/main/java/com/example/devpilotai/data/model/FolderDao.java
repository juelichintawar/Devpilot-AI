package com.example.devpilotai.data.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FolderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Folder folder);

    @Update
    void update(Folder folder);

    @Delete
    void delete(Folder folder);

    @Query("SELECT * FROM folders ORDER BY name ASC")
    LiveData<List<Folder>> getAllFolders();

    @Query("SELECT * FROM folders WHERE id = :id")
    LiveData<Folder> getFolderById(int id);
}
