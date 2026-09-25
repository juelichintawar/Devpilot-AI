package com.example.devpilotai.ui.chat;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.devpilotai.ai.GeminiAIService;
import com.example.devpilotai.data.ChatLogRepository;
import com.example.devpilotai.data.ChatMessageRepository;
import com.example.devpilotai.data.StatsRepository;
import com.example.devpilotai.data.model.ChatLog;
import com.example.devpilotai.data.model.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChatViewModel extends AndroidViewModel {

    private static final String TAG = "GeminiChat";

    private final ChatMessageRepository repository;
    private final StatsRepository statsRepository;
    private final ChatLogRepository chatLogRepository;
    private final GeminiAIService geminiAIService;

    private final LiveData<java.util.List<ChatMessage>> messages;

    public LiveData<java.util.List<ChatMessage>> getMessages() {
        return messages;
    }

    private final MutableLiveData<Boolean> _isLoading =
            new MutableLiveData<>(false);

    public LiveData<Boolean> isLoading = _isLoading;

    public ChatViewModel(@NonNull Application application) {
        super(application);

        repository = new ChatMessageRepository(application);
        statsRepository = new StatsRepository(application);
        chatLogRepository = new ChatLogRepository();

        geminiAIService = new GeminiAIService();

        messages = repository.getAllMessages();
    }

    public void sendMessage(String text) {

        if (text == null || text.trim().isEmpty()) {
            return;
        }

        String cleanText = text.trim();

        // Get current Firebase user
        FirebaseUser user =
                FirebaseAuth.getInstance().getCurrentUser();

        String userEmail =
                (user != null && user.getEmail() != null)
                        ? user.getEmail()
                        : "Anonymous";

        // Show user's message immediately
        ChatMessage userMessage =
                new ChatMessage(
                        cleanText,
                        ChatMessage.TYPE_USER
                );

        repository.insert(userMessage);

        // Show loading state
        _isLoading.setValue(true);

        Log.d(TAG, "Sending message to Firebase AI Logic");

        // Send message to Gemini through Firebase AI Logic
        geminiAIService.generateResponse(
                cleanText,
                new GeminiAIService.AIResponseCallback() {

                    @Override
                    public void onSuccess(String response) {

                        _isLoading.postValue(false);

                        // Add AI response to local chat
                        ChatMessage aiMessage =
                                new ChatMessage(
                                        response,
                                        ChatMessage.TYPE_AI
                                );

                        repository.insert(aiMessage);

                        // Save successful request in Firestore
                        chatLogRepository.logChat(
                                new ChatLog(
                                        userEmail,
                                        cleanText,
                                        response,
                                        "SUCCESS",
                                        null,
                                        System.currentTimeMillis(),
                                        0
                                )
                        );

                        Log.d(TAG, "AI response received successfully");
                    }

                    @Override
                    public void onError(Throwable error) {

                        _isLoading.postValue(false);

                        Log.e(
                                TAG,
                                "Firebase AI request failed",
                                error
                        );

                        String errorMessage =
                                "AI request failed. Please try again.";

                        if (error.getMessage() != null &&
                                !error.getMessage().isEmpty()) {

                            Log.e(
                                    TAG,
                                    "Error details: " + error.getMessage()
                            );
                        }

                        addErrorMessage(errorMessage);

                        // Save failed request in Firestore
                        chatLogRepository.logChat(
                                new ChatLog(
                                        userEmail,
                                        cleanText,
                                        "",
                                        "FAILED",
                                        error.getMessage(),
                                        System.currentTimeMillis(),
                                        0
                                )
                        );
                    }
                }
        );
    }

    private void addErrorMessage(String error) {

        repository.insert(
                new ChatMessage(
                        error,
                        ChatMessage.TYPE_AI
                )
        );
    }
}