package com.example.devpilotai.data.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ChallengeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Challenge challenge);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Challenge> challenges);

    @Query("SELECT * FROM challenges")
    LiveData<List<Challenge>> getAllChallenges();

    @Query("SELECT * FROM challenges WHERE difficulty = :difficulty")
    LiveData<List<Challenge>> getChallengesByDifficulty(String difficulty);

    @Query("SELECT * FROM challenges ORDER BY RANDOM() LIMIT 1")
    LiveData<Challenge> getRandomChallenge();

    @Query("SELECT * FROM challenges WHERE id = :id")
    LiveData<Challenge> getChallengeById(int id);
}
