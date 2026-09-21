package com.example.devpilotai.data;

import com.example.devpilotai.data.model.ChatLog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatLogRepository {
    private final FirebaseFirestore db;
    private final CollectionReference logsRef;

    public ChatLogRepository() {
        db = FirebaseFirestore.getInstance();
        logsRef = db.collection("chat_logs");
    }

    public void logChat(ChatLog log) {
        logsRef.add(log);
    }

    public void getAllLogs(LogsCallback callback) {
        logsRef.orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<ChatLog> logs = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ChatLog log = document.toObject(ChatLog.class);
                            log.setId(document.getId());
                            logs.add(log);
                        }
                        callback.onSuccess(logs);
                    } else {
                        callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Failed to fetch logs");
                    }
                });
    }

    public interface LogsCallback {
        void onSuccess(List<ChatLog> logs);
        void onFailure(String message);
    }
}
