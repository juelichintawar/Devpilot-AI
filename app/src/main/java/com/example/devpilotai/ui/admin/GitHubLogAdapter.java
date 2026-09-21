package com.example.devpilotai.ui.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.devpilotai.R;
import com.example.devpilotai.data.model.GitHubLog;
import com.example.devpilotai.databinding.ItemGithubLogBinding;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GitHubLogAdapter extends RecyclerView.Adapter<GitHubLogAdapter.LogViewHolder> {

    private List<GitHubLog> logs = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public void setLogs(List<GitHubLog> logs) {
        this.logs = logs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGithubLogBinding binding = ItemGithubLogBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new LogViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        holder.bind(logs.get(position));
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    class LogViewHolder extends RecyclerView.ViewHolder {
        private final ItemGithubLogBinding binding;

        LogViewHolder(ItemGithubLogBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(GitHubLog log) {
            binding.tvUserEmail.setText(log.getUserEmail());
            binding.tvLogAction.setText(log.getAction());
            binding.tvLogDetails.setText(log.getDetails());
            binding.tvLogTime.setText(dateFormat.format(new Date(log.getTimestamp())));
            
            boolean isSuccess = "SUCCESS".equalsIgnoreCase(log.getStatus());
            binding.tvLogStatus.setText(log.getStatus());
            binding.tvLogStatus.setBackgroundColor(itemView.getContext().getColor(isSuccess ? R.color.accent_green : R.color.error));
        }
    }
}
