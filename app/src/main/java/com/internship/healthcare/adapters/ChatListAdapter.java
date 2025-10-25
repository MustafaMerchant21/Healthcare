package com.internship.healthcare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.internship.healthcare.R;
import com.internship.healthcare.models.Chat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
/**
 * ChatListAdapter.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.adapters
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private List<Chat> chats;
    private OnChatClickListener listener;

    public interface OnChatClickListener {
        void onChatClick(Chat chat);
    }

/**
 * RecyclerView adapter managing chat data binding and view recycling.
 *
 * <p>Extends: {@link RecyclerView.Adapter}</p>
 * 
 * @author Mustafa Merchant
 * @version 1.0
 */
    public ChatListAdapter(OnChatClickListener listener) {
        this.chats = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message_card, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.bind(chats.get(position));
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }
    public void updateChats(List<Chat> newChats) {
        this.chats = newChats;
        notifyDataSetChanged();
    }

    private String formatTime(long timestamp) {
        Date messageDate = new Date(timestamp);
        Date today = new Date();

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());

        if (isSameDay(messageDate, today)) {
            return timeFormat.format(messageDate);
        } else {
            return dateFormat.format(messageDate);
        }
    }

    private boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return fmt.format(date1).equals(fmt.format(date2));
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        View onlineIndicator;
        TextView nameText, lastMessageText, timeText;
        TextView unreadBadge;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            onlineIndicator = itemView.findViewById(R.id.online_indicator);
            nameText = itemView.findViewById(R.id.name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            timeText = itemView.findViewById(R.id.time_text);
            unreadBadge = itemView.findViewById(R.id.notification_badge);
        }

        public void bind(Chat chat) {
            if (chat.getOtherUserName() != null && !chat.getOtherUserName().isEmpty()) {
                nameText.setText(chat.getOtherUserName());
            } else {
                nameText.setText("Unknown User");
            }

            Glide.with(itemView.getContext())
                    .load(chat.getOtherUserImage())
                    .circleCrop()
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .into(profileImage);

            String lastMsg = chat.getLastMessage();
            if (lastMsg != null && !lastMsg.isEmpty()) {
                lastMessageText.setText(lastMsg);
            } else {
                lastMessageText.setText("No messages yet");
            }

            long timestamp = chat.getLastMessageTime();
            if (timestamp > 0) {
                timeText.setText(formatTime(timestamp));
            } else {
                timeText.setText("");
            }

            // Online status
            if (onlineIndicator != null) {
                onlineIndicator.setVisibility(chat.isOnline() ? View.VISIBLE : View.GONE);
            }

            // Unread badge
            if (unreadBadge != null) {
                int unreadCount = chat.getUnreadCount();
                if (unreadCount > 0) {
                    unreadBadge.setVisibility(View.VISIBLE);
                    unreadBadge.setText(unreadCount > 99 ? "99+" : String.valueOf(unreadCount));
                } else {
                    unreadBadge.setVisibility(View.GONE);
                }
            }
                if (listener != null) {
                    listener.onChatClick(chat);
                }
        }
    }
}