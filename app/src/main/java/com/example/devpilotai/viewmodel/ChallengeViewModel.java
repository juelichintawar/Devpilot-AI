package com.example.devpilotai.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.devpilotai.ai.GeminiAIService;
import com.example.devpilotai.data.ChallengeRepository;
import com.example.devpilotai.data.model.Challenge;
import com.example.devpilotai.data.model.ChallengeResult;

import org.json.JSONObject;

import java.util.List;

public class ChallengeViewModel extends AndroidViewModel {

    private static final String TAG = "ChallengeViewModel";

    private final ChallengeRepository repository;
    private final GeminiAIService geminiAIService;

    private final MutableLiveData<Challenge> currentChallenge =
            new MutableLiveData<>();

    private final MutableLiveData<Integer> timerValue =
            new MutableLiveData<>();

    private final MutableLiveData<Boolean> isChallengeActive =
            new MutableLiveData<>(false);

    private final MutableLiveData<Boolean> isEvaluating =
            new MutableLiveData<>(false);

    private final MutableLiveData<EvaluationResult> evaluationResult =
            new MutableLiveData<>();

    public ChallengeViewModel(@NonNull Application application) {

        super(application);

        repository = new ChallengeRepository(application);

        repository.initSampleChallenges();

        // Firebase AI Logic
        geminiAIService = new GeminiAIService();
    }

    public LiveData<List<Challenge>> getChallengesByDifficulty(
            String difficulty) {

        return repository.getChallengesByDifficulty(difficulty);
    }

    public LiveData<Challenge> getRandomChallenge() {

        return repository.getRandomChallenge();
    }

    public LiveData<Challenge> getChallengeById(int id) {

        return repository.getChallengeById(id);
    }

    public void insertResult(ChallengeResult result) {

        repository.insertResult(result);
    }

    public LiveData<Integer> getCompletedCount() {

        return repository.getCompletedCount();
    }

    public LiveData<Integer> getTotalScore() {

        return repository.getTotalScore();
    }

    public LiveData<List<ChallengeResult>> getAllResults() {

        return repository.getAllResults();
    }

    public void setCurrentChallenge(Challenge challenge) {

        currentChallenge.setValue(challenge);

        timerValue.setValue(
                challenge.getTimeLimit()
        );
    }

    public LiveData<Challenge> getCurrentChallenge() {

        return currentChallenge;
    }

    public LiveData<Integer> getTimerValue() {

        return timerValue;
    }

    public void updateTimer(int value) {

        timerValue.setValue(value);
    }

    public LiveData<Boolean> getIsChallengeActive() {

        return isChallengeActive;
    }

    public void setChallengeActive(boolean active) {

        isChallengeActive.setValue(active);
    }

    public LiveData<Boolean> getIsEvaluating() {

        return isEvaluating;
    }

    public LiveData<EvaluationResult> getEvaluationResult() {

        return evaluationResult;
    }

    public void evaluateCode(
            String title,
            String description,
            String code) {

        isEvaluating.setValue(true);

        String prompt =
                "You are an AI code compiler and judge. "
                        + "Evaluate the following coding challenge submission.\n\n"

                        + "Challenge Title: "
                        + title
                        + "\n\n"

                        + "Challenge Description: "
                        + description
                        + "\n\n"

                        + "User Submission (Code):\n"
                        + code
                        + "\n\n"

                        + "Instructions:\n"
                        + "1. Mentally compile and run the code against "
                        + "3-4 diverse test cases, including edge cases.\n"

                        + "2. Determine whether the submission is correct.\n"

                        + "3. Return ONLY valid JSON.\n"

                        + "4. Use exactly these keys:\n"
                        + "   - status: SUCCESS or FAILURE\n"
                        + "   - message: A brief summary of the result.\n"
                        + "   - testCases: An array of objects containing "
                        + "input, expected, actual, and passed.\n"
                        + "   - compilerOutput: Compilation errors or "
                        + "warnings, if any.\n\n"

                        + "Example format:\n"
                        + "{"
                        + "\"status\":\"SUCCESS\","
                        + "\"message\":\"The solution passed all test cases.\","
                        + "\"testCases\":[],"
                        + "\"compilerOutput\":\"\""
                        + "}\n\n"

                        + "Return ONLY the JSON object.";

        Log.d(
                TAG,
                "Sending challenge evaluation to Firebase AI Logic"
        );

        geminiAIService.generateResponse(
                prompt,
                new GeminiAIService.AIResponseCallback() {

                    @Override
                    public void onSuccess(String response) {

                        isEvaluating.postValue(false);

                        try {

                            String text = response
                                    .replace("```json", "")
                                    .replace("```", "")
                                    .trim();

                            JSONObject json =
                                    new JSONObject(text);

                            boolean success =
                                    "SUCCESS".equalsIgnoreCase(
                                            json.optString("status")
                                    );

                            String message =
                                    json.optString(
                                            "message",
                                            "No evaluation message provided."
                                    );

                            String output =
                                    json.optString(
                                            "compilerOutput",
                                            ""
                                    );

                            evaluationResult.postValue(
                                    new EvaluationResult(
                                            success,
                                            message,
                                            output,
                                            text
                                    )
                            );

                        } catch (Exception e) {

                            Log.e(
                                    TAG,
                                    "Failed to parse AI evaluation",
                                    e
                            );

                            evaluationResult.postValue(
                                    new EvaluationResult(
                                            false,
                                            "Failed to parse AI response.",
                                            "",
                                            response
                                    )
                            );
                        }
                    }

                    @Override
                    public void onError(Throwable error) {

                        isEvaluating.postValue(false);

                        Log.e(
                                TAG,
                                "AI challenge evaluation failed",
                                error
                        );

                        String message =
                                "AI evaluation failed. Please try again.";

                        if (error.getMessage() != null &&
                                !error.getMessage().isEmpty()) {

                            Log.e(
                                    TAG,
                                    "Error details: "
                                            + error.getMessage()
                            );
                        }

                        evaluationResult.postValue(
                                new EvaluationResult(
                                        false,
                                        message,
                                        "",
                                        null
                                )
                        );
                    }
                }
        );
    }

    public static class EvaluationResult {

        public final boolean success;
        public final String message;
        public final String compilerOutput;
        public final String rawJson;

        public EvaluationResult(
                boolean success,
                String message,
                String compilerOutput,
                String rawJson) {

            this.success = success;
            this.message = message;
            this.compilerOutput = compilerOutput;
            this.rawJson = rawJson;
        }
    }
}