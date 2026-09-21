package com.example.devpilotai.data;

import com.example.devpilotai.data.model.GitHubLog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GitHubLogRepository {
    private final FirebaseFirestore db;
    private final CollectionReference logsRef;

    public GitHubLogRepository() {
        db = FirebaseFirestore.getInstance();
        logsRef = db.collection("github_logs");
    }

    public void logGitHubAction(GitHubLog log) {
        logsRef.add(log);
    }

    public void getAllLogs(LogsCallback callback) {
        logsRef.orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<GitHubLog> logs = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            GitHubLog log = document.toObject(GitHubLog.class);
                            log.setId(document.getId());
                            logs.add(log);
                        }
                        callback.onSuccess(logs);
                    } else {
                        callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Failed to fetch GitHub logs");
                    }
                });
    }

    public interface LogsCallback {
        void onSuccess(List<GitHubLog> logs);
        void onFailure(String message);
    }
}
