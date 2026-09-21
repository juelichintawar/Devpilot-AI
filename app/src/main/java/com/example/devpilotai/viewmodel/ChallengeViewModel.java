package com.example.devpilotai.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.devpilotai.BuildConfig;
import com.example.devpilotai.data.ChallengeRepository;
import com.example.devpilotai.data.api.RetrofitClient;
import com.example.devpilotai.data.model.Challenge;
import com.example.devpilotai.data.model.ChallengeResult;
import com.example.devpilotai.data.model.GeminiModels;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChallengeViewModel extends AndroidViewModel {
    private static final String TAG = "ChallengeViewModel";
    private final ChallengeRepository repository;
    private final MutableLiveData<Challenge> currentChallenge = new MutableLiveData<>();
    private final MutableLiveData<Integer> timerValue = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isChallengeActive = new MutableLiveData<>(false);
    
    private final MutableLiveData<Boolean> isEvaluating = new MutableLiveData<>(false);
    private final MutableLiveData<EvaluationResult> evaluationResult = new MutableLiveData<>();

    public ChallengeViewModel(@NonNull Application application) {
        super(application);
        repository = new ChallengeRepository(application);
        repository.initSampleChallenges();
    }

    public LiveData<List<Challenge>> getChallengesByDifficulty(String difficulty) {
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
        timerValue.setValue(challenge.getTimeLimit());
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

    public void evaluateCode(String title, String description, String code) {
        isEvaluating.setValue(true);

        String prompt = "You are an AI code compiler and judge. Evaluate the following coding challenge submission.\n\n" +
                "Challenge Title: " + title + "\n" +
                "Challenge Description: " + description + "\n" +
                "User Submission (Code): \n" + code + "\n\n" +
                "Instructions:\n" +
                "1. Mentally compile and run the code against 3-4 diverse test cases (including edge cases).\n" +
                "2. Provide the result in a strict JSON format with the following keys:\n" +
                "   - 'status': 'SUCCESS' or 'FAILURE'\n" +
                "   - 'message': A brief summary of the result.\n" +
                "   - 'testCases': An array of objects, each with 'input', 'expected', 'actual', and 'passed' (boolean).\n" +
                "   - 'compilerOutput': Any compilation errors or warnings (if any).\n\n" +
                "Return ONLY the JSON.";

        // Disable safety filters to ensure the AI answers all queries
        List<GeminiModels.SafetySetting> safetySettings = Arrays.asList(
                new GeminiModels.SafetySetting("HARM_CATEGORY_HARASSMENT", "BLOCK_NONE"),
                new GeminiModels.SafetySetting("HARM_CATEGORY_HATE_SPEECH", "BLOCK_NONE"),
                new GeminiModels.SafetySetting("HARM_CATEGORY_SEXUALLY_EXPLICIT", "BLOCK_NONE"),
                new GeminiModels.SafetySetting("HARM_CATEGORY_DANGEROUS_CONTENT", "BLOCK_NONE")
        );

        GeminiModels.Request request = new GeminiModels.Request(
                Collections.singletonList(new GeminiModels.Content("user", 
                        Collections.singletonList(new GeminiModels.Part(prompt)))),
                safetySettings,
                null
        );

        String rawKey = BuildConfig.GEMINI_API_KEY;
        String apiKey = null;
        String authHeader = null;

        // Handle both API Key (AIza) and Token (AQ) formats
        if (rawKey != null && !rawKey.isEmpty()) {
            if (rawKey.startsWith("AIza")) {
                apiKey = rawKey;
            } else {
                authHeader = "Bearer " + rawKey;
            }
        }

        RetrofitClient.getGeminiApiService()
                .generateContent("gemini-1.5-flash", apiKey, authHeader, request)
                .enqueue(new Callback<GeminiModels.Response>() {
                    @Override
                    public void onResponse(@NonNull Call<GeminiModels.Response> call, @NonNull Response<GeminiModels.Response> response) {
                        isEvaluating.setValue(false);
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                if (response.body().candidates == null || response.body().candidates.isEmpty()) {
                                    evaluationResult.setValue(new EvaluationResult(false, "AI returned no candidates. Possible safety block.", "", null));
                                    return;
                                }

                                String text = response.body().candidates.get(0).content.parts.get(0).text;
                                text = text.replace("```json", "").replace("```", "").trim();
                                
                                JSONObject json = new JSONObject(text);
                                boolean success = json.getString("status").equals("SUCCESS");
                                String message = json.getString("message");
                                String output = json.optString("compilerOutput", "");
                                
                                evaluationResult.setValue(new EvaluationResult(success, message, output, text));
                            } catch (Exception e) {
                                Log.e(TAG, "Parsing error", e);
                                evaluationResult.setValue(new EvaluationResult(false, "Failed to parse AI response.", "", null));
                            }
                        } else {
                            evaluationResult.setValue(new EvaluationResult(false, "AI evaluation failed. HTTP " + response.code(), "", null));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeminiModels.Response> call, @NonNull Throwable t) {
                        isEvaluating.setValue(false);
                        evaluationResult.setValue(new EvaluationResult(false, "Network error: " + t.getMessage(), "", null));
                    }
                });
    }

    public static class EvaluationResult {
        public final boolean success;
        public final String message;
        public final String compilerOutput;
        public final String rawJson;

        public EvaluationResult(boolean success, String message, String compilerOutput, String rawJson) {
            this.success = success;
            this.message = message;
            this.compilerOutput = compilerOutput;
            this.rawJson = rawJson;
        }
    }
}
