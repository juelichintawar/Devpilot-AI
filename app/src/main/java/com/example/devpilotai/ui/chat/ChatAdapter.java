package com.example.devpilotai.ui.chat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.devpilotai.R;
import com.example.devpilotai.data.model.ChatMessage;
import com.google.android.material.button.MaterialButton;

import io.noties.markwon.Markwon;

/**
 * Optimized adapter for Chat messages using ListAdapter and DiffUtil.
 * Handles different view types for User and AI messages with Markdown support.
 */
public class ChatAdapter extends ListAdapter<ChatMessage, RecyclerView.ViewHolder> {

    private final Markwon markwon;

    public ChatAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.markwon = Markwon.builder(context).build();
    }

    private static final DiffUtil.ItemCallback<ChatMessage> DIFF_CALLBACK = new DiffUtil.ItemCallback<ChatMessage>() {
        @Override
        public boolean areItemsTheSame(@NonNull ChatMessage oldItem, @NonNull ChatMessage newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ChatMessage oldItem, @NonNull ChatMessage newItem) {
            return oldItem.getContent().equals(newItem.getContent()) &&
                    oldItem.getType() == newItem.getType();
        }
    };

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == ChatMessage.TYPE_USER) {
            View view = inflater.inflate(R.layout.item_chat_user, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_chat_ai, parent, false);
            return new AiViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = getItem(position);

        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).bind(message);
        } else if (holder instanceof AiViewHolder) {
            ((AiViewHolder) holder).bind(message, markwon);
        }
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView textMessage;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
        }

        void bind(ChatMessage message) {
            textMessage.setText(message.getContent());
        }
    }

    static class AiViewHolder extends RecyclerView.ViewHolder {
        private final TextView textMessage;
        private final MaterialButton buttonCopy;
        private final MaterialButton buttonShare;

        AiViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
            buttonCopy = itemView.findViewById(R.id.buttonCopy);
            buttonShare = itemView.findViewById(R.id.buttonShare);
        }

        void bind(ChatMessage message, Markwon markwon) {
            markwon.setMarkdown(textMessage, message.getContent());

            buttonCopy.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("AI Response", message.getContent());
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(v.getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            });

            buttonShare.setOnClickListener(v -> {
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, message.getContent());
                sendIntent.setType("text/plain");
                v.getContext().startActivity(Intent.createChooser(sendIntent, "Share AI Response"));
            });
        }
    }
}
