package com.example.devpilotai.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.devpilotai.data.api.GitHubApiService;
import com.example.devpilotai.data.api.RetrofitClient;
import com.example.devpilotai.data.model.GitHubBranch;
import com.example.devpilotai.data.model.GitHubCommit;
import com.example.devpilotai.data.model.GitHubRepo;
import com.example.devpilotai.data.model.GitHubSearchResponse;
import com.example.devpilotai.data.model.GitHubTreeResponse;
import com.example.devpilotai.data.model.GitHubUser;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GitHubRepository {

    private final GitHubApiService apiService;
    private String authToken;

    public GitHubRepository() {
        this.apiService = RetrofitClient.getGitHubApiService();
    }

    public void setAuthToken(String token) {
        if (token != null && !token.startsWith("token ")) {
            this.authToken = "token " + token;
        } else {
            this.authToken = token;
        }
    }

    public LiveData<GitHubUser> loginWithToken(String token) {
        MutableLiveData<GitHubUser> userLiveData = new MutableLiveData<>();
        apiService.getAuthenticatedUser("token " + token).enqueue(new Callback<GitHubUser>() {
            @Override
            public void onResponse(Call<GitHubUser> call, Response<GitHubUser> response) {
                if (response.isSuccessful()) {
                    userLiveData.setValue(response.body());
                } else {
                    userLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<GitHubUser> call, Throwable t) {
                userLiveData.setValue(null);
            }
        });
        return userLiveData;
    }

    public LiveData<GitHubUser> getAuthenticatedUser() {
        MutableLiveData<GitHubUser> userLiveData = new MutableLiveData<>();
        apiService.getAuthenticatedUser(authToken).enqueue(new Callback<GitHubUser>() {
            @Override
            public void onResponse(Call<GitHubUser> call, Response<GitHubUser> response) {
                if (response.isSuccessful()) {
                    userLiveData.setValue(response.body());
                } else {
                    userLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<GitHubUser> call, Throwable t) {
                userLiveData.setValue(null);
            }
        });
        return userLiveData;
    }

    public LiveData<List<GitHubRepo>> getRepositories() {
        MutableLiveData<List<GitHubRepo>> reposLiveData = new MutableLiveData<>();
        apiService.getRepositories(authToken, "updated", 100).enqueue(new Callback<List<GitHubRepo>>() {
            @Override
            public void onResponse(Call<List<GitHubRepo>> call, Response<List<GitHubRepo>> response) {
                if (response.isSuccessful()) {
                    reposLiveData.setValue(response.body());
                } else {
                    reposLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<GitHubRepo>> call, Throwable t) {
                reposLiveData.setValue(null);
            }
        });
        return reposLiveData;
    }

    public LiveData<GitHubRepo> getRepositoryDetails(String owner, String repoName) {
        MutableLiveData<GitHubRepo> repoLiveData = new MutableLiveData<>();
        apiService.getRepositoryDetails(authToken, owner, repoName).enqueue(new Callback<GitHubRepo>() {
            @Override
            public void onResponse(Call<GitHubRepo> call, Response<GitHubRepo> response) {
                if (response.isSuccessful()) {
                    repoLiveData.setValue(response.body());
                } else {
                    repoLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<GitHubRepo> call, Throwable t) {
                repoLiveData.setValue(null);
            }
        });
        return repoLiveData;
    }

    public LiveData<List<GitHubCommit>> getCommits(String owner, String repoName) {
        MutableLiveData<List<GitHubCommit>> commitsLiveData = new MutableLiveData<>();
        apiService.getCommits(authToken, owner, repoName).enqueue(new Callback<List<GitHubCommit>>() {
            @Override
            public void onResponse(Call<List<GitHubCommit>> call, Response<List<GitHubCommit>> response) {
                if (response.isSuccessful()) {
                    commitsLiveData.setValue(response.body());
                } else {
                    commitsLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<GitHubCommit>> call, Throwable t) {
                commitsLiveData.setValue(null);
            }
        });
        return commitsLiveData;
    }

    public LiveData<List<GitHubBranch>> getBranches(String owner, String repoName) {
        MutableLiveData<List<GitHubBranch>> branchesLiveData = new MutableLiveData<>();
        apiService.getBranches(authToken, owner, repoName).enqueue(new Callback<List<GitHubBranch>>() {
            @Override
            public void onResponse(Call<List<GitHubBranch>> call, Response<List<GitHubBranch>> response) {
                if (response.isSuccessful()) {
                    branchesLiveData.setValue(response.body());
                } else {
                    branchesLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<GitHubBranch>> call, Throwable t) {
                branchesLiveData.setValue(null);
            }
        });
        return branchesLiveData;
    }

    public LiveData<List<GitHubRepo>> searchRepositories(String query) {
        MutableLiveData<List<GitHubRepo>> searchResultLiveData = new MutableLiveData<>();
        apiService.searchRepositories(authToken, query).enqueue(new Callback<GitHubSearchResponse>() {
            @Override
            public void onResponse(Call<GitHubSearchResponse> call, Response<GitHubSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    searchResultLiveData.setValue(response.body().getItems());
                } else {
                    searchResultLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<GitHubSearchResponse> call, Throwable t) {
                searchResultLiveData.setValue(null);
            }
        });
        return searchResultLiveData;
    }

    public LiveData<String> getReadme(String owner, String repoName) {
        MutableLiveData<String> readmeLiveData = new MutableLiveData<>();
        apiService.getReadme(authToken, "application/vnd.github.html", owner, repoName).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        readmeLiveData.setValue(response.body().string());
                    } catch (IOException e) {
                        readmeLiveData.setValue(null);
                    }
                } else {
                    readmeLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                readmeLiveData.setValue(null);
            }
        });
        return readmeLiveData;
    }

    public LiveData<List<Map<String, Object>>> getIssues(String owner, String repoName) {
        MutableLiveData<List<Map<String, Object>>> issuesLiveData = new MutableLiveData<>();
        apiService.getIssues(authToken, owner, repoName).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful()) {
                    issuesLiveData.setValue(response.body());
                } else {
                    issuesLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                issuesLiveData.setValue(null);
            }
        });
        return issuesLiveData;
    }

    public LiveData<List<Map<String, Object>>> getUserActivity(String username) {
        MutableLiveData<List<Map<String, Object>>> activityLiveData = new MutableLiveData<>();
        apiService.getUserEvents(authToken, username).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful()) {
                    activityLiveData.setValue(response.body());
                } else {
                    activityLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                activityLiveData.setValue(null);
            }
        });
        return activityLiveData;
    }

    public LiveData<GitHubTreeResponse> getTree(String owner, String repo, String sha, boolean recursive) {
        MutableLiveData<GitHubTreeResponse> treeLiveData = new MutableLiveData<>();
        apiService.getTree(authToken, owner, repo, sha, recursive ? 1 : 0).enqueue(new Callback<GitHubTreeResponse>() {
            @Override
            public void onResponse(Call<GitHubTreeResponse> call, Response<GitHubTreeResponse> response) {
                if (response.isSuccessful()) {
                    treeLiveData.setValue(response.body());
                } else {
                    treeLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<GitHubTreeResponse> call, Throwable t) {
                treeLiveData.setValue(null);
            }
        });
        return treeLiveData;
    }

    public LiveData<String> getRawContent(String url) {
        MutableLiveData<String> rawContentLiveData = new MutableLiveData<>();
        apiService.getRawBlob(authToken, "application/vnd.github.raw", url).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        rawContentLiveData.setValue(response.body().string());
                    } catch (IOException e) {
                        rawContentLiveData.setValue(null);
                    }
                } else {
                    rawContentLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                rawContentLiveData.setValue(null);
            }
        });
        return rawContentLiveData;
    }
}
