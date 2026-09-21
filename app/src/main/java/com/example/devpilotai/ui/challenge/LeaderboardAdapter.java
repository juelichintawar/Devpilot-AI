package com.example.devpilotai.ui.challenge;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.devpilotai.data.model.ChallengeResult;
import com.example.devpilotai.databinding.ItemLeaderboardBinding;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    private List<ChallengeResult> results = new ArrayList<>();

    public void setResults(List<ChallengeResult> results) {
        this.results = results != null ? results : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLeaderboardBinding binding = ItemLeaderboardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new LeaderboardViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        holder.bind(results.get(position), position + 4); // +4 because Top 3 are in the podium
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        private final ItemLeaderboardBinding binding;

        public LeaderboardViewHolder(ItemLeaderboardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ChallengeResult result, int rank) {
            binding.tvRank.setText(String.valueOf(rank));
            binding.tvUserName.setText(result.getUserEmail().split("@")[0]);
            binding.tvUserEmail.setText(result.getUserEmail());
            binding.tvScore.setText(String.valueOf(result.getScore()));
        }
    }
}
