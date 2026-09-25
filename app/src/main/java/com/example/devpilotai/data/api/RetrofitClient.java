package com.example.devpilotai.data.api;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton client for Retrofit.
 *
 * Retrofit is used for GitHub API calls.
 * Gemini requests are handled through Firebase AI Logic.
 */
public class RetrofitClient {

    private static final String GITHUB_BASE_URL =
            "https://api.github.com/";

    private static GitHubApiService gitHubApiService;

    private static OkHttpClient getClient() {

        HttpLoggingInterceptor logging =
                new HttpLoggingInterceptor(message ->
                        Log.d("OkHttp", message)
                );

        logging.setLevel(
                HttpLoggingInterceptor.Level.BODY
        );

        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    public static synchronized GitHubApiService getGitHubApiService() {

        if (gitHubApiService == null) {

            Retrofit retrofit =
                    new Retrofit.Builder()
                            .baseUrl(GITHUB_BASE_URL)
                            .client(getClient())
                            .addConverterFactory(
                                    GsonConverterFactory.create()
                            )
                            .build();

            gitHubApiService =
                    retrofit.create(GitHubApiService.class);
        }

        return gitHubApiService;
    }
}