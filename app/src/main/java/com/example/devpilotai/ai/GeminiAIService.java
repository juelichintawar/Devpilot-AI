package com.example.devpilotai.ai;

import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.ChatFutures;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerativeBackend;

public class GeminiAIService {

    private static final String TAG = "GeminiAIService";

    private final ChatFutures chat;

    public GeminiAIService() {

        Log.d(TAG, "Initializing Firebase AI Logic...");

        GenerativeModel ai =
                FirebaseAI
                        .getInstance(GenerativeBackend.googleAI())
                        .generativeModel("gemini-3.5-flash-lite");

        GenerativeModelFutures model =
                GenerativeModelFutures.from(ai);

        chat = model.startChat();

        Log.d(TAG, "Firebase AI Logic initialized successfully");
    }

    public void generateResponse(
            String prompt,
            AIResponseCallback callback
    ) {

        Log.d(TAG, "Sending prompt to Gemini: " + prompt);

        try {

            Content message =
                    new Content.Builder()
                            .setRole("user")
                            .addText(prompt)
                            .build();

            ListenableFuture<GenerateContentResponse> response =
                    chat.sendMessage(message);

            Log.d(TAG, "Gemini request sent successfully");

            Futures.addCallback(
                    response,
                    new FutureCallback<GenerateContentResponse>() {

                        @Override
                        public void onSuccess(
                                GenerateContentResponse result) {

                            Log.d(
                                    TAG,
                                    "GEMINI SUCCESS CALLBACK"
                            );

                            try {

                                String text = result.getText();

                                Log.d(
                                        TAG,
                                        "Gemini response: " + text
                                );

                                if (text == null ||
                                        text.trim().isEmpty()) {

                                    Log.e(
                                            TAG,
                                            "Gemini returned an empty response"
                                    );

                                    callback.onError(
                                            new Exception(
                                                    "AI returned an empty response."
                                            )
                                    );

                                    return;
                                }

                                callback.onSuccess(text);

                            } catch (Exception e) {

                                Log.e(
                                        TAG,
                                        "Error processing Gemini response",
                                        e
                                );

                                callback.onError(e);
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {

                            Log.e(
                                    TAG,
                                    "GEMINI FAILURE CALLBACK",
                                    t
                            );

                            String errorMessage =
                                    t.getMessage();

                            if (errorMessage != null) {

                                Log.e(
                                        TAG,
                                        "Gemini error details: "
                                                + errorMessage
                                );
                            }

                            callback.onError(t);
                        }
                    },
                    MoreExecutors.directExecutor()
            );

        } catch (Exception e) {

            Log.e(
                    TAG,
                    "Exception while sending Gemini request",
                    e
            );

            callback.onError(e);
        }
    }

    public interface AIResponseCallback {

        void onSuccess(String response);

        void onError(Throwable error);
    }
}
