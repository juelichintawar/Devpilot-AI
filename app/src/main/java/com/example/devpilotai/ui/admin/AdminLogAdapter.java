package com.example.devpilotai.ui.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.devpilotai.R;
import com.example.devpilotai.data.model.AdminLog;
import com.example.devpilotai.databinding.ItemAdminLogBinding;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminLogAdapter extends RecyclerView.Adapter<AdminLogAdapter.LogViewHolder> {

    private List<AdminLog> logs = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public void setLogs(List<AdminLog> logs) {
        this.logs = logs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminLogBinding binding = ItemAdminLogBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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
        private final ItemAdminLogBinding binding;

        LogViewHolder(ItemAdminLogBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(AdminLog log) {
            binding.tvAdminEmail.setText(log.getAdminEmail());
            binding.tvLogAction.setText(log.getAction());
            binding.tvLogDetails.setText(log.getDetails());
            binding.tvLogTime.setText(dateFormat.format(new Date(log.getTimestamp())));
            
            if (log.getTarget() != null && !log.getTarget().isEmpty()) {
                binding.tvLogTarget.setVisibility(android.view.View.VISIBLE);
                binding.tvLogTarget.setText("Target: " + log.getTarget());
            } else {
                binding.tvLogTarget.setVisibility(android.view.View.GONE);
            }

            // Set color based on action type
            int colorRes = R.color.primary;
            if (log.getAction().contains("DELETE")) {
                colorRes = R.color.error;
            } else if (log.getAction().contains("DEACTIVATE")) {
                colorRes = R.color.accent_orange;
            } else if (log.getAction().contains("ACTIVATE")) {
                colorRes = R.color.accent_green;
            }
            binding.tvLogAction.setBackgroundTintList(android.content.res.ColorStateList.valueOf(itemView.getContext().getColor(colorRes)));
        }
    }
}
