package com.example.devpilotai.data.api;

import com.example.devpilotai.data.model.GitHubBranch;
import com.example.devpilotai.data.model.GitHubCommit;
import com.example.devpilotai.data.model.GitHubRepo;
import com.example.devpilotai.data.model.GitHubSearchResponse;
import com.example.devpilotai.data.model.GitHubTreeResponse;
import com.example.devpilotai.data.model.GitHubUser;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface GitHubApiService {

    @GET("user")
    Call<GitHubUser> getAuthenticatedUser(@Header("Authorization") String token);

    @GET("user/repos")
    Call<List<GitHubRepo>> getRepositories(
            @Header("Authorization") String token,
            @Query("sort") String sort,
            @Query("per_page") int perPage
    );

    @GET("repos/{owner}/{repo}")
    Call<GitHubRepo> getRepositoryDetails(
            @Header("Authorization") String token,
            @Path("owner") String owner,
            @Path("repo") String repo
    );

    @GET("repos/{owner}/{repo}/commits")
    Call<List<GitHubCommit>> getCommits(
            @Header("Authorization") String token,
            @Path("owner") String owner,
            @Path("repo") String repo
    );

    @GET("repos/{owner}/{repo}/branches")
    Call<List<GitHubBranch>> getBranches(
            @Header("Authorization") String token,
            @Path("owner") String owner,
            @Path("repo") String repo
    );

    @GET("search/repositories")
    Call<GitHubSearchResponse> searchRepositories(
            @Header("Authorization") String token,
            @Query("q") String query
    );

    @GET("repos/{owner}/{repo}/readme")
    Call<ResponseBody> getReadme(
            @Header("Authorization") String token,
            @Header("Accept") String accept,
            @Path("owner") String owner,
            @Path("repo") String repo
    );

    @GET("repos/{owner}/{repo}/issues")
    Call<List<Map<String, Object>>> getIssues(
            @Header("Authorization") String token,
            @Path("owner") String owner,
            @Path("repo") String repo
    );

    @GET("users/{username}/events")
    Call<List<Map<String, Object>>> getUserEvents(
            @Header("Authorization") String token,
            @Path("username") String username
    );

    @GET("repos/{owner}/{repo}/git/trees/{sha}")
    Call<GitHubTreeResponse> getTree(
            @Header("Authorization") String token,
            @Path("owner") String owner,
            @Path("repo") String repo,
            @Path("sha") String sha,
            @Query("recursive") int recursive
    );

    @GET
    Call<ResponseBody> getRawBlob(
            @Header("Authorization") String token,
            @Header("Accept") String accept,
            @Url String url
    );
}
