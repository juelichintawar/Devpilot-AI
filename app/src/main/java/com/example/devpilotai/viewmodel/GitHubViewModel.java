package com.example.devpilotai.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.devpilotai.BuildConfig;
import com.example.devpilotai.data.GitHubLogRepository;
import com.example.devpilotai.data.GitHubRepository;
import com.example.devpilotai.data.UserRepository;
import com.example.devpilotai.data.api.RetrofitClient;
import com.example.devpilotai.data.local.TokenManager;
import com.example.devpilotai.data.model.GeminiModels;
import com.example.devpilotai.data.model.GitHubBranch;
import com.example.devpilotai.data.model.GitHubCommit;
import com.example.devpilotai.data.model.GitHubLog;
import com.example.devpilotai.data.model.GitHubRepo;
import com.example.devpilotai.data.model.GitHubTreeResponse;
import com.example.devpilotai.data.model.GitHubUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GitHubViewModel extends AndroidViewModel {

    private static final String TAG = "GitHubViewModel";
    private final GitHubRepository repository;
    private final TokenManager tokenManager;
    private final GitHubLogRepository logRepository;
    private final UserRepository userRepository;
    
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<GitHubUser> userProfile = new MutableLiveData<>();
    private final MutableLiveData<String> aiAnalysisResult = new MutableLiveData<>();

    public GitHubViewModel(@NonNull Application application) {
        super(application);
        repository = new GitHubRepository();
        tokenManager = TokenManager.getInstance(application);
        logRepository = new GitHubLogRepository();
        userRepository = new UserRepository();
        
        if (tokenManager.hasToken()) {
            repository.setAuthToken(tokenManager.getToken());
        }
    }

    private String getCurrentUserEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return (user != null) ? user.getEmail() : "Anonymous";
    }

    private String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    public boolean isLoggedIn() {
        return tokenManager.hasToken();
    }

    public void setToken(String token) {
        tokenManager.saveToken(token);
        repository.setAuthToken(token);
    }

    public void logout() {
        String email = getCurrentUserEmail();
        String uid = getCurrentUserId();
        
        tokenManager.deleteToken();
        repository.setAuthToken(null);
        userProfile.setValue(null);
        
        logRepository.logGitHubAction(new GitHubLog(email, "LOGOUT", "User logged out from GitHub", "SUCCESS", System.currentTimeMillis()));
        
        if (uid != null) {
            userRepository.updateGitHubConnectionStatus(uid, false);
        }
    }

    public LiveData<GitHubUser> login(String token) {
        isLoading.setValue(true);
        MutableLiveData<GitHubUser> loginResult = new MutableLiveData<>();
        String email = getCurrentUserEmail();
        String uid = getCurrentUserId();
        
        repository.loginWithToken(token).observeForever(user -> {
            isLoading.setValue(false);
            if (user != null) {
                setToken(token);
                userProfile.setValue(user);
                logRepository.logGitHubAction(new GitHubLog(email, "LOGIN", "Connected as " + user.getLogin(), "SUCCESS", System.currentTimeMillis()));
                if (uid != null) {
                    userRepository.updateGitHubConnectionStatus(uid, true);
                }
            } else {
                logRepository.logGitHubAction(new GitHubLog(email, "LOGIN", "Failed to connect with token", "FAILED", System.currentTimeMillis()));
            }
            loginResult.setValue(user);
        });
        return loginResult;
    }

    public LiveData<GitHubUser> getUserProfile() {
        if (userProfile.getValue() == null && isLoggedIn()) {
            refreshUserProfile();
        }
        return userProfile;
    }

    public void refreshUserProfile() {
        isLoading.setValue(true);
        repository.getAuthenticatedUser().observeForever(user -> {
            isLoading.setValue(false);
            if (user != null) {
                userProfile.setValue(user);
            } else {
                error.setValue("Failed to fetch profile");
            }
        });
    }

    public LiveData<List<GitHubRepo>> getRepositories() {
        isLoading.setValue(true);
        MutableLiveData<List<GitHubRepo>> result = new MutableLiveData<>();
        String email = getCurrentUserEmail();
        
        repository.getRepositories().observeForever(repos -> {
            isLoading.setValue(false);
            if (repos != null) {
                logRepository.logGitHubAction(new GitHubLog(email, "FETCH_REPOS", "Fetched " + repos.size() + " repositories", "SUCCESS", System.currentTimeMillis()));
            } else {
                logRepository.logGitHubAction(new GitHubLog(email, "FETCH_REPOS", "Failed to fetch repositories", "FAILED", System.currentTimeMillis()));
            }
            result.setValue(repos);
        });
        return result;
    }

    public LiveData<GitHubRepo> getRepositoryDetails(String owner, String repoName) {
        isLoading.setValue(true);
        MutableLiveData<GitHubRepo> result = new MutableLiveData<>();
        repository.getRepositoryDetails(owner, repoName).observeForever(repo -> {
            isLoading.setValue(false);
            result.setValue(repo);
        });
        return result;
    }

    public LiveData<List<GitHubCommit>> getCommits(String owner, String repoName) {
        isLoading.setValue(true);
        MutableLiveData<List<GitHubCommit>> result = new MutableLiveData<>();
        repository.getCommits(owner, repoName).observeForever(commits -> {
            isLoading.setValue(false);
            result.setValue(commits);
        });
        return result;
    }

    public LiveData<List<GitHubBranch>> getBranches(String owner, String repoName) {
        isLoading.setValue(true);
        MutableLiveData<List<GitHubBranch>> result = new MutableLiveData<>();
        repository.getBranches(owner, repoName).observeForever(branches -> {
            isLoading.setValue(false);
            result.setValue(branches);
        });
        return result;
    }

    public LiveData<List<GitHubRepo>> searchRepositories(String query) {
        isLoading.setValue(true);
        MutableLiveData<List<GitHubRepo>> result = new MutableLiveData<>();
        repository.searchRepositories(query).observeForever(repos -> {
            isLoading.setValue(false);
            result.setValue(repos);
        });
        return result;
    }

    public LiveData<String> getReadme(String owner, String repoName) {
        isLoading.setValue(true);
        MutableLiveData<String> result = new MutableLiveData<>();
        repository.getReadme(owner, repoName).observeForever(readme -> {
            isLoading.setValue(false);
            result.setValue(readme);
        });
        return result;
    }

    public LiveData<List<Map<String, Object>>> getIssues(String owner, String repoName) {
        isLoading.setValue(true);
        MutableLiveData<List<Map<String, Object>>> result = new MutableLiveData<>();
        repository.getIssues(owner, repoName).observeForever(issues -> {
            isLoading.setValue(false);
            result.setValue(issues);
        });
        return result;
    }

    public LiveData<List<Map<String, Object>>> getUserActivity(String username) {
        isLoading.setValue(true);
        MutableLiveData<List<Map<String, Object>>> result = new MutableLiveData<>();
        repository.getUserActivity(username).observeForever(activities -> {
            isLoading.setValue(false);
            result.setValue(activities);
        });
        return result;
    }

    public LiveData<String> getAiAnalysisResult() {
        return aiAnalysisResult;
    }

    public void analyzeRepository(String owner, String repoName) {
        if (isLoading.getValue() != null && isLoading.getValue()) return;
        
        isLoading.setValue(true);
        aiAnalysisResult.setValue(null);
        error.setValue(null);

        String token = tokenManager.getToken();
        String authHeader = (token != null) ? "token " + token : null;
        String email = getCurrentUserEmail();

        logRepository.logGitHubAction(new GitHubLog(email, "ANALYZE_REPO", "Started analysis for " + owner + "/" + repoName, "SUCCESS", System.currentTimeMillis()));

        RetrofitClient.getGitHubApiService().getReadme(authHeader, "application/vnd.github.html", owner, repoName)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String readme = "";
                        try {
                            if (response.isSuccessful() && response.body() != null) {
                                readme = response.body().string();
                            }
                        } catch (IOException ignored) {}
                        
                        fetchTree(owner, repoName, authHeader, readme);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        fetchTree(owner, repoName, authHeader, "");
                    }
                });
    }

    private void fetchTree(String owner, String repoName, String authHeader, String readme) {
        RetrofitClient.getGitHubApiService().getTree(authHeader, owner, repoName, "main", 1)
                .enqueue(new Callback<GitHubTreeResponse>() {
                    @Override
                    public void onResponse(Call<GitHubTreeResponse> call, Response<GitHubTreeResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            fetchFilesAndAnalyze(owner, repoName, authHeader, readme, response.body());
                        } else {
                            RetrofitClient.getGitHubApiService().getTree(authHeader, owner, repoName, "master", 1)
                                    .enqueue(new Callback<GitHubTreeResponse>() {
                                        @Override
                                        public void onResponse(Call<GitHubTreeResponse> call, Response<GitHubTreeResponse> responseMaster) {
                                            if (responseMaster.isSuccessful() && responseMaster.body() != null) {
                                                fetchFilesAndAnalyze(owner, repoName, authHeader, readme, responseMaster.body());
                                            } else {
                                                isLoading.setValue(false);
                                                String errorMsg = "Failed to fetch repository structure";
                                                error.setValue(errorMsg);
                                                logRepository.logGitHubAction(new GitHubLog(getCurrentUserEmail(), "ANALYZE_REPO", errorMsg, "FAILED", System.currentTimeMillis()));
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<GitHubTreeResponse> call, Throwable t) {
                                            isLoading.setValue(false);
                                            error.setValue("Network error: " + t.getMessage());
                                            logRepository.logGitHubAction(new GitHubLog(getCurrentUserEmail(), "ANALYZE_REPO", t.getMessage(), "FAILED", System.currentTimeMillis()));
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onFailure(Call<GitHubTreeResponse> call, Throwable t) {
                        isLoading.setValue(false);
                        error.setValue("Network error: " + t.getMessage());
                        logRepository.logGitHubAction(new GitHubLog(getCurrentUserEmail(), "ANALYZE_REPO", t.getMessage(), "FAILED", System.currentTimeMillis()));
                    }
                });
    }

    private void fetchFilesAndAnalyze(String owner, String repoName, String authHeader, String readme, GitHubTreeResponse treeResponse) {
        final StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("Project: ").append(owner).append("/").append(repoName).append("\n\n");
        if (!readme.isEmpty()) {
            contextBuilder.append("README:\n").append(readme).append("\n\n");
        }
        contextBuilder.append("SOURCE FILES:\n");

        List<GitHubTreeResponse.TreeEntry> entries = treeResponse.tree;
        int maxFiles = 5;
        AtomicInteger filesFetched = new AtomicInteger(0);
        
        int filesToFetchCount = 0;
        for (GitHubTreeResponse.TreeEntry entry : entries) {
            if ("blob".equals(entry.type) && isSourceFile(entry.path)) {
                filesToFetchCount++;
                if (filesToFetchCount >= maxFiles) break;
            }
        }

        if (filesToFetchCount == 0) {
            callGeminiForAnalysis(contextBuilder.toString(), owner, repoName);
            return;
        }

        final int totalToFetch = filesToFetchCount;
        int count = 0;
        for (GitHubTreeResponse.TreeEntry entry : entries) {
            if ("blob".equals(entry.type) && isSourceFile(entry.path)) {
                final String path = entry.path;
                RetrofitClient.getGitHubApiService().getRawBlob(authHeader, "application/vnd.github.raw", entry.url)
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    if (response.isSuccessful() && response.body() != null) {
                                        contextBuilder.append("File: ").append(path).append("\nContent:\n")
                                                .append(response.body().string()).append("\n\n");
                                    }
                                } catch (IOException ignored) {}
                                
                                if (filesFetched.incrementAndGet() == totalToFetch) {
                                    callGeminiForAnalysis(contextBuilder.toString(), owner, repoName);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                if (filesFetched.incrementAndGet() == totalToFetch) {
                                    callGeminiForAnalysis(contextBuilder.toString(), owner, repoName);
                                }
                            }
                        });
                count++;
                if (count >= maxFiles) break;
            }
        }
    }

    private boolean isSourceFile(String path) {
        String lower = path.toLowerCase();
        if (lower.contains("gradle") || lower.contains("node_modules") || lower.contains("test") || lower.startsWith(".")) {
            return false;
        }
        return lower.endsWith(".java") || lower.endsWith(".kt") || lower.endsWith(".js") || 
               lower.endsWith(".py") || lower.endsWith(".cpp") || lower.endsWith(".c") ||
               lower.endsWith(".ts") || lower.endsWith(".go");
    }

    private void callGeminiForAnalysis(String context, String owner, String repoName) {
        String prompt = "Review this GitHub repository based on the provided README and source code snippets. " +
                "Provide a structured review including:\n" +
                "1. Code Quality Score (0-100)\n" +
                "2. Architecture Suggestions\n" +
                "3. Security Issues\n" +
                "4. Performance Improvements\n" +
                "5. Resume-worthy project summary (highlighting key skills used)\n\n" +
                "CONTEXT:\n" + context;

        List<GeminiModels.SafetySetting> safetySettings = Arrays.asList(
                new GeminiModels.SafetySetting("HARM_CATEGORY_HARASSMENT", "BLOCK_NONE"),
                new GeminiModels.SafetySetting("HARM_CATEGORY_HATE_SPEECH", "BLOCK_NONE"),
                new GeminiModels.SafetySetting("HARM_CATEGORY_SEXUALLY_EXPLICIT", "BLOCK_NONE"),
                new GeminiModels.SafetySetting("HARM_CATEGORY_DANGEROUS_CONTENT", "BLOCK_NONE")
        );

        GeminiModels.Request request = new GeminiModels.Request(
                Collections.singletonList(new GeminiModels.Content("user", 
                        Collections.singletonList(new GeminiModels.Part(prompt)))),
                safetySettings,
                null
        );

        String rawKey = BuildConfig.GEMINI_API_KEY;
        String apiKey = null;
        String authHeader = null;

        if (rawKey != null && !rawKey.isEmpty()) {
            if (rawKey.startsWith("AIza")) {
                apiKey = rawKey;
            } else {
                authHeader = "Bearer " + rawKey;
            }
        }

        // Fixed: Added model parameter "gemini-1.5-flash" and auth params to match GeminiApiService
        RetrofitClient.getGeminiApiService().generateContent("gemini-1.5-flash", apiKey, authHeader, request)
                .enqueue(new Callback<GeminiModels.Response>() {
                    @Override
                    public void onResponse(Call<GeminiModels.Response> call, Response<GeminiModels.Response> response) {
                        isLoading.setValue(false);
                        if (response.isSuccessful() && response.body() != null) {
                            GeminiModels.Response body = response.body();
                            if (body.candidates != null && !body.candidates.isEmpty() &&
                                body.candidates.get(0).content != null &&
                                body.candidates.get(0).content.parts != null &&
                                !body.candidates.get(0).content.parts.isEmpty()) {
                                
                                String text = body.candidates.get(0).content.parts.get(0).text;
                                aiAnalysisResult.setValue(text);
                                logRepository.logGitHubAction(new GitHubLog(getCurrentUserEmail(), "ANALYZE_REPO", "Successfully analyzed " + owner + "/" + repoName, "SUCCESS", System.currentTimeMillis()));
                            } else {
                                String errorMsg = "AI Analysis failed to return content. Possible safety block.";
                                error.setValue(errorMsg);
                                logRepository.logGitHubAction(new GitHubLog(getCurrentUserEmail(), "ANALYZE_REPO", errorMsg, "FAILED", System.currentTimeMillis()));
                            }
                        } else {
                            String errorMsg = "AI Analysis failed. HTTP " + response.code();
                            error.setValue(errorMsg);
                            logRepository.logGitHubAction(new GitHubLog(getCurrentUserEmail(), "ANALYZE_REPO", errorMsg, "FAILED", System.currentTimeMillis()));
                        }
                    }

                    @Override
                    public void onFailure(Call<GeminiModels.Response> call, Throwable t) {
                        isLoading.setValue(false);
                        error.setValue("Network error during analysis: " + t.getMessage());
                        logRepository.logGitHubAction(new GitHubLog(getCurrentUserEmail(), "ANALYZE_REPO", t.getMessage(), "FAILED", System.currentTimeMillis()));
                    }
                });
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }
}
