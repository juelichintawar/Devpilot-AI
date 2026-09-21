package com.example.devpilotai.data.api;

import android.util.Log;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton client for Retrofit to ensure efficient resource usage.
 */
public class RetrofitClient {

    private static final String TAG = "RetrofitClient";
    private static final String GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/";
    private static final String GITHUB_BASE_URL = "https://api.github.com/";

    private static GeminiApiService geminiApiService;
    private static GitHubApiService gitHubApiService;

    private static OkHttpClient getClient() {
        // Requirement 10: Redact API key from Logcat to prevent exposure.
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> {
            // Regex to catch 'key=...' and replace the entire value regardless of character type
            String redacted = message.replaceAll("key=[^&\\s]+", "key=REDACTED");
            Log.d("OkHttp", redacted);
        });
        
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    public static synchronized GeminiApiService getGeminiApiService() {
        if (geminiApiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(GEMINI_BASE_URL)
                    .client(getClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            geminiApiService = retrofit.create(GeminiApiService.class);
        }
        return geminiApiService;
    }

    public static synchronized GitHubApiService getGitHubApiService() {
        if (gitHubApiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(GITHUB_BASE_URL)
                    .client(getClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            gitHubApiService = retrofit.create(GitHubApiService.class);
        }
        return gitHubApiService;
    }
}
