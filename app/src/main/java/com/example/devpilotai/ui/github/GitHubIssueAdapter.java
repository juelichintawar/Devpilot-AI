package com.example.devpilotai.ui.github;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.devpilotai.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GitHubIssueAdapter extends RecyclerView.Adapter<GitHubIssueAdapter.IssueViewHolder> {

    private List<Map<String, Object>> issues = new ArrayList<>();

    public void setIssues(List<Map<String, Object>> issues) {
        this.issues = issues != null ? issues : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IssueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_github_issue, parent, false);
        return new IssueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IssueViewHolder holder, int position) {
        Map<String, Object> issue = issues.get(position);
        String title = (String) issue.get("title");
        Double number = (Double) issue.get("number");
        String state = (String) issue.get("state");

        holder.issueTitle.setText(title);
        holder.issueNumber.setText("#" + (number != null ? number.intValue() : ""));
        holder.issueState.setText(state);
    }

    @Override
    public int getItemCount() {
        return issues.size();
    }

    static class IssueViewHolder extends RecyclerView.ViewHolder {
        TextView issueTitle, issueNumber, issueState;

        public IssueViewHolder(@NonNull View itemView) {
            super(itemView);
            issueTitle = itemView.findViewById(R.id.issueTitle);
            issueNumber = itemView.findViewById(R.id.issueNumber);
            issueState = itemView.findViewById(R.id.issueState);
        }
    }
}
