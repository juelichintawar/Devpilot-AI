package com.example.devpilotai.data.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserStatsDao {
    @Query("SELECT * FROM user_stats WHERE id = 1")
    LiveData<UserStats> getUserStats();

    @Query("SELECT * FROM user_stats WHERE id = 1")
    UserStats getUserStatsSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserStats stats);

    @Update
    void update(UserStats stats);

    @Query("UPDATE user_stats SET totalTokensUsed = totalTokensUsed + :tokens WHERE id = 1")
    void incrementTokens(int tokens);

    @Query("UPDATE user_stats SET totalSnippetsSaved = totalSnippetsSaved + 1 WHERE id = 1")
    void incrementSnippets();

    @Query("UPDATE user_stats SET totalOcrScans = totalOcrScans + 1 WHERE id = 1")
    void incrementOcrScans();

    @Query("UPDATE user_stats SET totalNotesCreated = totalNotesCreated + 1 WHERE id = 1")
    void incrementNotes();
}
