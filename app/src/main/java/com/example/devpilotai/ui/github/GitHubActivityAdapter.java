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

public class GitHubActivityAdapter extends RecyclerView.Adapter<GitHubActivityAdapter.ActivityViewHolder> {

    private List<Map<String, Object>> activities = new ArrayList<>();

    public void setActivities(List<Map<String, Object>> activities) {
        this.activities = activities != null ? activities : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        Map<String, Object> activity = activities.get(position);
        String type = (String) activity.get("type");
        Map<String, Object> repo = (Map<String, Object>) activity.get("repo");
        String repoName = repo != null ? (String) repo.get("name") : "Unknown Repo";
        
        holder.text1.setText(type);
        holder.text2.setText(repoName);
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }
    }
}
