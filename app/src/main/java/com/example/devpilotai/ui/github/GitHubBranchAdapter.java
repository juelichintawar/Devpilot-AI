package com.example.devpilotai.ui.github;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.devpilotai.R;
import com.example.devpilotai.data.model.GitHubBranch;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class GitHubBranchAdapter extends RecyclerView.Adapter<GitHubBranchAdapter.BranchViewHolder> {

    private List<GitHubBranch> branches = new ArrayList<>();

    public void setBranches(List<GitHubBranch> branches) {
        this.branches = branches != null ? branches : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BranchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_github_branch, parent, false);
        return new BranchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BranchViewHolder holder, int position) {
        GitHubBranch branch = branches.get(position);
        holder.bind(branch);
    }

    @Override
    public int getItemCount() {
        return branches.size();
    }

    static class BranchViewHolder extends RecyclerView.ViewHolder {
        private final Chip branchChip;

        public BranchViewHolder(@NonNull View itemView) {
            super(itemView);
            branchChip = (Chip) itemView;
        }

        public void bind(GitHubBranch branch) {
            branchChip.setText(branch.getName());
        }
    }
}
