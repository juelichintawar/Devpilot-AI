package com.example.devpilotai.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.devpilotai.data.model.Challenge;
import com.example.devpilotai.data.model.ChallengeDao;
import com.example.devpilotai.data.model.ChallengeResult;
import com.example.devpilotai.data.model.ChallengeResultDao;
import com.example.devpilotai.utils.AppExecutors;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing Coding Challenges and Results.
 * Centralizes data access and employs AppExecutors for background tasks.
 */
public class ChallengeRepository {
    private final ChallengeDao challengeDao;
    private final ChallengeResultDao resultDao;
    private final AppExecutors appExecutors;

    public ChallengeRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        challengeDao = db.challengeDao();
        resultDao = db.challengeResultDao();
        appExecutors = AppExecutors.getInstance();
    }

    public LiveData<List<Challenge>> getAllChallenges() {
        return challengeDao.getAllChallenges();
    }

    public LiveData<List<Challenge>> getChallengesByDifficulty(String difficulty) {
        return challengeDao.getChallengesByDifficulty(difficulty);
    }

    public LiveData<Challenge> getRandomChallenge() {
        return challengeDao.getRandomChallenge();
    }

    public LiveData<Challenge> getChallengeById(int id) {
        return challengeDao.getChallengeById(id);
    }

    public void insertResult(ChallengeResult result) {
        appExecutors.diskIO().execute(() -> resultDao.insert(result));
    }

    public LiveData<Integer> getCompletedCount() {
        return resultDao.getCompletedCount();
    }

    public LiveData<Integer> getTotalScore() {
        return resultDao.getTotalScore();
    }

    public LiveData<List<ChallengeResult>> getAllResults() {
        return resultDao.getAllResults();
    }

    /**
     * Seeds the database with initial coding challenges.
     */
    public void initSampleChallenges() {
        appExecutors.diskIO().execute(() -> {
            // In production, this might come from a remote config or bundled JSON
            List<Challenge> challenges = new ArrayList<>();
            challenges.add(new Challenge("Reverse a String", "Write a function that reverses a string.", "Easy", "Algorithms", "input: 'hello', output: ?", "olleh", 60));
            challenges.add(new Challenge("Two Sum", "Find two numbers that add up to a specific target.", "Easy", "Data Structures", "nums = [2,7,11,15], target = 9", "[0,1]", 120));
            challenges.add(new Challenge("Palindrome Number", "Determine if an integer is a palindrome.", "Easy", "Logic", "input: 121", "true", 90));
            
            challenges.add(new Challenge("Valid Parentheses", "Check if brackets are closed in correct order.", "Medium", "Stack", "input: '()[]{}'", "true", 180));
            challenges.add(new Challenge("Merge Sorted Lists", "Merge two sorted linked lists.", "Medium", "Linked List", "L1=[1,2,4], L2=[1,3,4]", "[1,1,2,3,4,4]", 240));
            
            challenges.add(new Challenge("Median of Two Sorted Arrays", "Find the median of two sorted arrays.", "Hard", "Arrays", "nums1 = [1,3], nums2 = [2]", "2.0", 600));
            challenges.add(new Challenge("Trapping Rain Water", "Calculate how much water can be trapped after raining.", "Hard", "Two Pointers", "height = [0,1,0,2,1,0,1,3,2,1,2,1]", "6", 900));
            
            challengeDao.insertAll(challenges);
        });
    }
}
