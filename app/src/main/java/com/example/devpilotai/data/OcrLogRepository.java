package com.example.devpilotai.data;

import com.example.devpilotai.data.model.OcrLog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OcrLogRepository {
    private final FirebaseFirestore db;
    private final CollectionReference logsRef;

    public OcrLogRepository() {
        db = FirebaseFirestore.getInstance();
        logsRef = db.collection("ocr_logs");
    }

    public void logOcr(OcrLog log) {
        logsRef.add(log);
    }

    public void getAllLogs(LogsCallback callback) {
        logsRef.orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<OcrLog> logs = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            OcrLog log = document.toObject(OcrLog.class);
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
        void onSuccess(List<OcrLog> logs);
        void onFailure(String message);
    }
}
