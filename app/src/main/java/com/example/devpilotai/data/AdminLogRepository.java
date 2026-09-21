package com.example.devpilotai.data;

import com.example.devpilotai.data.model.AdminLog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminLogRepository {
    private final FirebaseFirestore db;
    private final CollectionReference logsRef;

    public AdminLogRepository() {
        db = FirebaseFirestore.getInstance();
        logsRef = db.collection("admin_activity_logs");
    }

    public void logAction(AdminLog log) {
        logsRef.add(log);
    }

    public void logAction(String action, String adminEmail, String details) {
        logAction(new AdminLog(adminEmail, action, details, null, System.currentTimeMillis()));
    }

    public void logAction(String action, String adminEmail, String details, String target) {
        logAction(new AdminLog(adminEmail, action, details, target, System.currentTimeMillis()));
    }

    public void getAllLogs(LogsCallback callback) {
        logsRef.orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<AdminLog> logs = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            AdminLog log = document.toObject(AdminLog.class);
                            log.setId(document.getId());
                            logs.add(log);
                        }
                        callback.onSuccess(logs);
                    } else {
                        callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Failed to fetch admin logs");
                    }
                });
    }

    public interface LogsCallback {
        void onSuccess(List<AdminLog> logs);
        void onFailure(String message);
    }
}
