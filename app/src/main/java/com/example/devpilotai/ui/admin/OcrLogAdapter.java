package com.example.devpilotai.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.devpilotai.R;
import com.example.devpilotai.data.model.OcrLog;
import com.example.devpilotai.databinding.ItemOcrLogBinding;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OcrLogAdapter extends RecyclerView.Adapter<OcrLogAdapter.OcrViewHolder> {

    private List<OcrLog> logs = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public void setLogs(List<OcrLog> logs) {
        this.logs = logs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OcrViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOcrLogBinding binding = ItemOcrLogBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new OcrViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OcrViewHolder holder, int position) {
        holder.bind(logs.get(position));
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    class OcrViewHolder extends RecyclerView.ViewHolder {
        private final ItemOcrLogBinding binding;

        OcrViewHolder(ItemOcrLogBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(OcrLog log) {
            binding.tvUserEmail.setText(log.getUserEmail());
            binding.tvLogSnippet.setText(log.getResultSnippet() != null ? log.getResultSnippet() : "N/A");
            binding.tvLogTime.setText(dateFormat.format(new Date(log.getTimestamp())));
            
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
