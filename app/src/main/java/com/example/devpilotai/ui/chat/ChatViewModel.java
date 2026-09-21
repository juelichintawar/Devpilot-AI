package com.example.devpilotai.ui.chat;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.devpilotai.BuildConfig;
import com.example.devpilotai.data.ChatLogRepository;
import com.example.devpilotai.data.ChatMessageRepository;
import com.example.devpilotai.data.StatsRepository;
import com.example.devpilotai.data.api.RetrofitClient;
import com.example.devpilotai.data.model.ChatLog;
import com.example.devpilotai.data.model.ChatMessage;
import com.example.devpilotai.data.model.GeminiModels;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatViewModel extends AndroidViewModel {

    private static final String TAG = "GeminiChat";
    private static final String MODEL_NAME = "gemini-1.5-flash";

    private final ChatMessageRepository repository;
    private final StatsRepository statsRepository;
    private final ChatLogRepository chatLogRepository;

    private final LiveData<List<ChatMessage>> messages;
    public LiveData<List<ChatMessage>> getMessages() { return messages; }

    private final MutableLiveData<Boolean> _isLoading =
            new MutableLiveData<>(false);

    public LiveData<Boolean> isLoading = _isLoading;

    private final List<GeminiModels.Content> history =
            new ArrayList<>();

    public ChatViewModel(@NonNull Application application) {
        super(application);
        repository = new ChatMessageRepository(application);
        statsRepository = new StatsRepository(application);
        chatLogRepository = new ChatLogRepository();
        messages = repository.getAllMessages();
    }

    public void sendMessage(String text) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }

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

        ChatMessage userMessage = new ChatMessage(text, ChatMessage.TYPE_USER);
        repository.insert(userMessage);

        history.add(new GeminiModels.Content("user", Collections.singletonList(new GeminiModels.Part(text))));

        limitHistory();
        _isLoading.setValue(true);

        List<GeminiModels.SafetySetting> safetySettings = Arrays.asList(
                new GeminiModels.SafetySetting("HARM_CATEGORY_HARASSMENT", "BLOCK_NONE"),
                new GeminiModels.SafetySetting("HARM_CATEGORY_HATE_SPEECH", "BLOCK_NONE"),
                new GeminiModels.SafetySetting("HARM_CATEGORY_SEXUALLY_EXPLICIT", "BLOCK_NONE"),
                new GeminiModels.SafetySetting("HARM_CATEGORY_DANGEROUS_CONTENT", "BLOCK_NONE")
        );

        GeminiModels.Request request = new GeminiModels.Request(
                new ArrayList<>(history),
                safetySettings,
                new GeminiModels.GenerationConfig(0.7, 2048)
        );
        
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = (user != null) ? user.getEmail() : "Anonymous";

        RetrofitClient.getGeminiApiService()
                .generateContent(MODEL_NAME, apiKey, authHeader, request)
                .enqueue(new Callback<GeminiModels.Response>() {
                    @Override
                    public void onResponse(@NonNull Call<GeminiModels.Response> call, @NonNull Response<GeminiModels.Response> response) {
                        _isLoading.setValue(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            GeminiModels.Response body = response.body();
                            
                            if (body.candidates != null && !body.candidates.isEmpty() &&
                                body.candidates.get(0).content != null &&
                                body.candidates.get(0).content.parts != null &&
                                !body.candidates.get(0).content.parts.isEmpty()) {
                                
                                String aiResponse = body.candidates.get(0).content.parts.get(0).text;
                                repository.insert(new ChatMessage(aiResponse, ChatMessage.TYPE_AI));
                                history.add(new GeminiModels.Content("model", Collections.singletonList(new GeminiModels.Part(aiResponse))));
                                limitHistory();
                                
                                chatLogRepository.logChat(new ChatLog(userEmail, text, aiResponse, "SUCCESS", null, System.currentTimeMillis(), 0));
                            } else {
                                addErrorMessage("AI returned an empty response. This might be due to safety filters.");
                                chatLogRepository.logChat(new ChatLog(userEmail, text, "", "BLOCKED", "Safety filters", System.currentTimeMillis(), 0));
                            }
                        } else {
                            int statusCode = response.code();
                            Log.e(TAG, "Request failed. Code: " + statusCode);
                            
                            if (statusCode == 404) {
                                addErrorMessage("Error: Model not found or API version mismatch (404). Please rebuild the project.");
                            } else if (statusCode == 401 || statusCode == 403) {
                                addErrorMessage("Authentication Failed. Please check your API Key.");
                            } else {
                                addErrorMessage("AI request failed (HTTP " + statusCode + ")");
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeminiModels.Response> call, @NonNull Throwable t) {
                        _isLoading.setValue(false);
                        Log.e(TAG, "Network error: " + t.getMessage());
                        addErrorMessage("Network error. Please check your connection.");
                    }
                });
    }

    private void limitHistory() {
        while (history.size() > 20) {
            history.remove(0);
        }
        while (!history.isEmpty() && !"user".equals(history.get(0).role)) {
            history.remove(0);
        }
    }

    private void addErrorMessage(String error) {
        repository.insert(new ChatMessage(error, ChatMessage.TYPE_AI));
    }
}
