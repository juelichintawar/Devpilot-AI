package com.example.devpilotai.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.devpilotai.R;
import com.example.devpilotai.data.model.ChatLog;
import com.example.devpilotai.databinding.ItemChatLogBinding;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatLogAdapter extends RecyclerView.Adapter<ChatLogAdapter.LogViewHolder> {

    private List<ChatLog> logs = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public void setLogs(List<ChatLog> logs) {
        this.logs = logs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChatLogBinding binding = ItemChatLogBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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
        private final ItemChatLogBinding binding;

        LogViewHolder(ItemChatLogBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ChatLog log) {
            binding.tvUserEmail.setText(log.getUserEmail());
            binding.tvLogPrompt.setText(log.getPrompt());
            binding.tvLogTime.setText(dateFormat.format(new Date(log.getTimestamp())));
            binding.tvLogTokens.setText(log.getTokens() + " Tokens");
            
            boolean isSuccess = "SUCCESS".equalsIgnoreCase(log.getStatus());
            binding.tvLogStatus.setText(log.getStatus());
            binding.tvLogStatus.setBackgroundColor(itemView.getContext().getColor(isSuccess ? R.color.accent_green : R.color.error));

            if (!isSuccess && log.getErrorMessage() != null) {
                binding.tvLogError.setVisibility(View.VISIBLE);
                binding.tvLogError.setText(log.getErrorMessage());
            } else {
                binding.tvLogError.setVisibility(View.GONE);
            }
        }
    }
}
