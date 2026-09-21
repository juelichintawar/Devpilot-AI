package com.example.devpilotai.data.api;

import com.example.devpilotai.data.model.GeminiModels;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GeminiApiService {

    /**
     * Gemini AI generateContent endpoint.
     * Switched to 'v1' for stable model support (gemini-1.5-flash).
     */
    @POST("v1/models/{model}:generateContent")
    Call<GeminiModels.Response> generateContent(
            @Path("model") String model,
            @Query("key") String apiKey,
            @Header("Authorization") String authHeader,
            @Body GeminiModels.Request request
    );
}
