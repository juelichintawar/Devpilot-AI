package com.example.devpilotai.ui.github;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.devpilotai.R;
import com.example.devpilotai.data.model.GitHubRepo;

import java.util.ArrayList;
import java.util.List;

public class GitHubRepoAdapter extends RecyclerView.Adapter<GitHubRepoAdapter.RepoViewHolder> {

    private List<GitHubRepo> repos = new ArrayList<>();
    private final OnRepoClickListener listener;

    public interface OnRepoClickListener {
        void onRepoClick(GitHubRepo repo);
    }

    public GitHubRepoAdapter(OnRepoClickListener listener) {
        this.listener = listener;
    }

    public void setRepos(List<GitHubRepo> repos) {
        this.repos = repos != null ? repos : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RepoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_github_repo, parent, false);
        return new RepoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RepoViewHolder holder, int position) {
        GitHubRepo repo = repos.get(position);
        holder.bind(repo, listener);
    }

    @Override
    public int getItemCount() {
        return repos.size();
    }

    static class RepoViewHolder extends RecyclerView.ViewHolder {
        private final TextView repoName;
        private final TextView repoDescription;
        private final TextView repoLanguage;
        private final TextView repoStars;
        private final TextView repoForks;

        public RepoViewHolder(@NonNull View itemView) {
            super(itemView);
            repoName = itemView.findViewById(R.id.repoName);
            repoDescription = itemView.findViewById(R.id.repoDescription);
            repoLanguage = itemView.findViewById(R.id.repoLanguage);
            repoStars = itemView.findViewById(R.id.repoStars);
            repoForks = itemView.findViewById(R.id.repoForks);
        }

        public void bind(GitHubRepo repo, OnRepoClickListener listener) {
            repoName.setText(repo.getName());
            repoDescription.setText(repo.getDescription() != null ? repo.getDescription() : "No description");
            repoLanguage.setText(repo.getLanguage() != null ? repo.getLanguage() : "N/A");
            repoStars.setText(String.valueOf(repo.getStargazersCount()));
            repoForks.setText(String.valueOf(repo.getForksCount()));
            
            itemView.setOnClickListener(v -> listener.onRepoClick(repo));
        }
    }
}
