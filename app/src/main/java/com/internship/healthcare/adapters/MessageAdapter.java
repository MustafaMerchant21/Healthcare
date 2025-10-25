package com.internship.healthcare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.internship.healthcare.R;
import com.internship.healthcare.models.Message;

import java.util.List;
/**
 * MessageAdapter.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.adapters
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final List<Message> messages;
    private final OnMessageClickListener clickListener;

    public interface OnMessageClickListener {
        void onMessageClick(Message message);
    }
    public MessageAdapter(List<Message> messages, OnMessageClickListener clickListener) {
        this.messages = messages;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message_card, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message, clickListener);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final ImageView doctorAvatar;
        private final TextView doctorName;
        private final TextView lastMessage;
        private final TextView timestamp;
        private final View onlineIndicator;
        private final TextView notificationBadge;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorAvatar = itemView.findViewById(R.id.doctor_avatar);
            doctorName = itemView.findViewById(R.id.doctor_name);
            lastMessage = itemView.findViewById(R.id.last_message_text);
            timestamp = itemView.findViewById(R.id.time_text);
            onlineIndicator = itemView.findViewById(R.id.online_indicator);
            notificationBadge = itemView.findViewById(R.id.notification_badge);
        }

        public void bind(Message message, OnMessageClickListener clickListener) {
            doctorName.setText(message.getDoctorName());
            lastMessage.setText(message.getLastMessage());
            timestamp.setText(message.getTimestamp());
            doctorAvatar.setImageResource(message.getDoctorImageResId());

            // Show/hide online indicator
            if (message.isOnline()) {
                onlineIndicator.setVisibility(View.VISIBLE);
            } else {
                onlineIndicator.setVisibility(View.GONE);
            }

            // Show/hide notification badge
            if (message.getUnreadCount() > 0) {
                notificationBadge.setVisibility(View.VISIBLE);
                notificationBadge.setText(String.valueOf(message.getUnreadCount()));
            } else {
                notificationBadge.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onMessageClick(message);
                }
            });
        }
    }
}