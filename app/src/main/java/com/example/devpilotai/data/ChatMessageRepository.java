package com.example.devpilotai.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.devpilotai.data.model.ChatMessage;
import com.example.devpilotai.data.model.ChatMessageDao;
import com.example.devpilotai.utils.AppExecutors;
import java.util.List;

/**
 * Repository for managing Chat history using Room database.
 * Uses centralized AppExecutors for background operations.
 */
public class ChatMessageRepository {
    private final ChatMessageDao chatMessageDao;
    private final AppExecutors appExecutors;

    public ChatMessageRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        chatMessageDao = db.chatMessageDao();
        appExecutors = AppExecutors.getInstance();
    }

    public void insert(ChatMessage message) {
        appExecutors.diskIO().execute(() -> chatMessageDao.insert(message));
    }

    public LiveData<List<ChatMessage>> getAllMessages() {
        return chatMessageDao.getAllMessages();
    }

    public LiveData<List<ChatMessage>> searchChatHistory(String query) {
        return chatMessageDao.searchChatHistory(query);
    }

    public void clearHistory() {
        appExecutors.diskIO().execute(chatMessageDao::deleteAllMessages);
    }
}
