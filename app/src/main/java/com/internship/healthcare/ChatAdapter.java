package com.internship.healthcare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
/**
 * ChatAdapter.java
 * A comprehensive healthcare management Android application
 * RecyclerView adapter managing chat data binding and view recycling.
 *
 * <p>Extends: {@link RecyclerView.Adapter}</p>
 * Package: com.internship.healthcare
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @Override

    public int getItemViewType(int position) {
        return messages.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        
        switch (viewType) {
            case ChatMessage.TYPE_DATE:
                View dateView = inflater.inflate(R.layout.item_chat_date, parent, false);
                return new DateViewHolder(dateView);
            
            case ChatMessage.TYPE_USER:
                View userView = inflater.inflate(R.layout.item_chat_user, parent, false);
    
                return new UserMessageViewHolder(userView);
            
            case ChatMessage.TYPE_DOCTOR:
            default:
                View doctorView = inflater.inflate(R.layout.item_chat_doctor, parent, false);
                return new DoctorMessageViewHolder(doctorView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        
        switch (holder.getItemViewType()) {
            case ChatMessage.TYPE_DATE:
                ((DateViewHolder) holder).bind(message);
                break;
            
            case ChatMessage.TYPE_USER:
                ((UserMessageViewHolder) holder).bind(message);
                break;
            
            case ChatMessage.TYPE_DOCTOR:
                ((DoctorMessageViewHolder) holder).bind(message);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    
    }

    // Date ViewHolder
    static class DateViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateText;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.date_text);
        }

        public void bind(ChatMessage message) {
            dateText.setText(message.getDateLabel());
        }
    }

    // User Message ViewHolder
    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageText;
        private final TextView messageTime;
        private final ImageView userAvatar;
        private final ImageView messageImage;

        public UserMessageViewHolder(@NonNull View itemView) {
    
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            messageTime = itemView.findViewById(R.id.message_time);
            userAvatar = itemView.findViewById(R.id.user_avatar);
            messageImage = itemView.findViewById(R.id.message_image);
        }

        public void bind(ChatMessage message) {
            messageText.setText(message.getMessage());
            messageTime.setText(message.getTimestamp());
            userAvatar.setImageResource(message.getUserAvatarResId());
            
            if (message.hasImage()) {
                messageImage.setVisibility(View.VISIBLE);
                 Glide.with(itemView.getContext()).load(message.getImageUrl()).into(messageImage);
            } else {
    
                messageImage.setVisibility(View.GONE);
            }
            
            if (message.getMessage() == null || message.getMessage().isEmpty()) {
                messageText.setVisibility(View.GONE);
            } else {
                messageText.setVisibility(View.VISIBLE);
            }
        }
    }

    // Doctor Message ViewHolder
    static class DoctorMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageText;
        private final TextView messageTime;
        private final ImageView doctorAvatar;
        private final ImageView messageImage;

        public DoctorMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            messageTime = itemView.findViewById(R.id.message_time);
            doctorAvatar = itemView.findViewById(R.id.doctor_avatar);
            messageImage = itemView.findViewById(R.id.message_image);
    
        }

        public void bind(ChatMessage message) {
            messageText.setText(message.getMessage());
            messageTime.setText(message.getTimestamp());
            doctorAvatar.setImageResource(message.getDoctorAvatarResId());
            
            if (message.hasImage()) {
                messageImage.setVisibility(View.VISIBLE);
                // TODO: Load image using Glide or Picasso
                // Glide.with(itemView.getContext()).load(message.getImageUrl()).into(messageImage);
            } else {
                messageImage.setVisibility(View.GONE);
            }
            
            if (message.getMessage() == null || message.getMessage().isEmpty()) {
                messageText.setVisibility(View.GONE);
            } else {
                messageText.setVisibility(View.VISIBLE);
            }
        }
    }

    // Method to add new message
    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    // Method to update messages
    public void updateMessages(List<ChatMessage> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }
}

    
    
    