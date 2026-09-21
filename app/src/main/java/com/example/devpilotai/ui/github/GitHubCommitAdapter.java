package com.example.devpilotai.ui.github;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.devpilotai.R;
import com.example.devpilotai.data.model.GitHubCommit;

import java.util.ArrayList;
import java.util.List;

public class GitHubCommitAdapter extends RecyclerView.Adapter<GitHubCommitAdapter.CommitViewHolder> {

    private List<GitHubCommit> commits = new ArrayList<>();

    public void setCommits(List<GitHubCommit> commits) {
        this.commits = commits != null ? commits : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_github_commit, parent, false);
        return new CommitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommitViewHolder holder, int position) {
        GitHubCommit commit = commits.get(position);
        holder.bind(commit);
    }

    @Override
    public int getItemCount() {
        return commits.size();
    }

    static class CommitViewHolder extends RecyclerView.ViewHolder {
        private final TextView commitMessage;
        private final TextView commitAuthorDate;

        public CommitViewHolder(@NonNull View itemView) {
            super(itemView);
            commitMessage = itemView.findViewById(R.id.commitMessage);
            commitAuthorDate = itemView.findViewById(R.id.commitAuthorDate);
        }

        public void bind(GitHubCommit commit) {
            commitMessage.setText(commit.getCommit().getMessage());
            String info = commit.getCommit().getAuthor().getName() + " committed on " + commit.getCommit().getAuthor().getDate();
            commitAuthorDate.setText(info);
        }
    }
}
