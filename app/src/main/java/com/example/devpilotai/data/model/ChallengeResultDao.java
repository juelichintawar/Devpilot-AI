package com.example.devpilotai.data.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ChallengeResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ChallengeResult result);

    @Query("SELECT * FROM challenge_results ORDER BY timestamp DESC")
    LiveData<List<ChallengeResult>> getAllResults();

    @Query("SELECT COUNT(*) FROM challenge_results WHERE isSuccess = 1")
    LiveData<Integer> getCompletedCount();

    @Query("SELECT SUM(score) FROM challenge_results")
    LiveData<Integer> getTotalScore();
}
