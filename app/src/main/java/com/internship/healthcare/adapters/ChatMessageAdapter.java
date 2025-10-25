package com.internship.healthcare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.internship.healthcare.R;
import com.internship.healthcare.models.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
/**
 * ChatMessageAdapter.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.adapters
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private List<ChatMessage> messages;
    private String currentUserId;
    private OnMessageClickListener listener;

/**
 * RecyclerView adapter managing chat message data binding and view recycling.
 *
 * <p>Extends: {@link Adapter}</p>
 * 
 * @author Mustafa Merchant
 * @version 1.0
 */
    public interface OnMessageClickListener {
        void onImageClick(String imageUrl);
    }

    public ChatMessageAdapter(String currentUserId, OnMessageClickListener listener) {
        this.messages = new ArrayList<>();
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        return message.getSenderId().equals(currentUserId) ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
    /**
     * Retrieves the item view type in patient information and records
     *
     * @param position integer value
     * @return integer result of the operation
     */
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).bind(message);
        } else if (holder instanceof ReceivedMessageViewHolder) {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void updateMessages(List<ChatMessage> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }

    public void addMessage(ChatMessage message) {
    /**
     * Callback invoked when bind view holder in patient information and records
     *
     * @param holder holder
     * @param position integer value
     */
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    private String formatTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ImageView messageImage, readIndicator;
        MaterialCardView messageCard, imageCard;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            timeText = itemView.findViewById(R.id.message_time);
            messageImage = itemView.findViewById(R.id.message_image);
            readIndicator = itemView.findViewById(R.id.read_indicator);
            messageCard = itemView.findViewById(R.id.message_card);
            imageCard = itemView.findViewById(R.id.image_card);
        }

        public void bind(ChatMessage message) {
            timeText.setText(formatTime(message.getTimestamp()));

            if ("image".equals(message.getMessageType())) {
                messageCard.setVisibility(View.GONE);
                imageCard.setVisibility(View.VISIBLE);
                
                Glide.with(itemView.getContext())
                        .load(message.getImageUrl())
                        .centerCrop()
                        .into(messageImage);

                imageCard.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onImageClick(message.getImageUrl());
                    }
                });
            } else {
                messageCard.setVisibility(View.VISIBLE);
                imageCard.setVisibility(View.GONE);
                messageText.setText(message.getMessage());
            }

            // Read indicator
            if (readIndicator != null) {
                readIndicator.setVisibility(message.isRead() ? View.VISIBLE : View.GONE);
            }
        }
    }

    class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ImageView messageImage;
        MaterialCardView messageCard, imageCard;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            timeText = itemView.findViewById(R.id.message_time);
            messageImage = itemView.findViewById(R.id.message_image);
            messageCard = itemView.findViewById(R.id.message_card);
            imageCard = itemView.findViewById(R.id.image_card);
        }

        public void bind(ChatMessage message) {
            timeText.setText(formatTime(message.getTimestamp()));

            if ("image".equals(message.getMessageType())) {
                messageCard.setVisibility(View.GONE);
                imageCard.setVisibility(View.VISIBLE);

                Glide.with(itemView.getContext())
                        .load(message.getImageUrl())
                        .centerCrop()
                        .into(messageImage);

                imageCard.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onImageClick(message.getImageUrl());
                    }
                });
            } else {
                messageCard.setVisibility(View.VISIBLE);
                imageCard.setVisibility(View.GONE);
                messageText.setText(message.getMessage());
            }
        }
    }
}